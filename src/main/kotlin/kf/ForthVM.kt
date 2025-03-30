package kf


import kf.consoles.IConsole
import kf.dict.Dict
import kf.dict.IWordModule
import kf.dict.Word
import kf.interps.IInterp
import kf.mem.CellMeta
import kf.mem.MemConfig
import kf.mem.RegisterDelegate
import kf.mem.smallMemConfig
import kf.sources.SourceBase
import kf.sources.SourceStdIn
import kf.stacks.FStack
import kf.words.core.ext.mCoreExt
import kf.words.core.mCore
import kf.words.custom.mCustom
import kf.words.doublenums.mDoubleNums
import kf.words.facility.mFacility
import kf.words.fileaccess.mFileAccess
import kf.words.tools.mTools
import kotlin.time.TimeSource


/**
 *  A Forth virtual machine.
 *
 *  This executes in Forth-memory code and
 *  manages Forth memory, registers, and stacks.
 *
 *  @property io [kf.consoles.Console] for this VM
 *  @property interp [kf.interps.InterpBase] for this VM
 *  @property memConfig [MemConfig] for this VM
 *  @property mem RAM for the VM
 *  @param initVerbosity Initial verbosity setting
 *  @param initSourceMaker Lambda VM will call after reboot to set input source
 */

class ForthVM(
    val io: IConsole,
    val interp: IInterp,
    val memConfig: MemConfig = smallMemConfig,
    val mem: IntArray = IntArray(memConfig.upperBound + 1),
    initVerbosity: Int = 1,
    val initSourceMaker: (ForthVM) -> SourceBase = { SourceStdIn(it) },
) {

    /** Which interpreter is active?
     *
     * The VM needs to point to the interp, and the interp needs to point to the
     * VM, so this is a lateinit--the creator of the VM will patch the interp
     * on here after making it.
     */


    // *************************************************************** registers

    /** Base of math output (10 is for decimal and default) */
    var base by RegisterDelegate(REG_BASE, this.mem)

    /** Verbosity of system:
     *
     * -  4  very low-level debugging
     * -  3  show internal msgs
     * -  2  show all words
     * -  1  welcome messages, user warnings, etc. (default)
     * -  0  no banner or unimportant warnings
     * - -1  quiet: no warnings
     * - -2  very no prompt, no output except direct (like .)
     *
     *  To see any dev-debugging messages, the global variable [D] needs to
     *  be true.
     */
    var verbosity by RegisterDelegate(REG_VERBOSITY, this.mem)

    /** Current end of the CODE section. */
    var cend by RegisterDelegate(REG_CEND, this.mem)

    /** Current end of the DATA section. */
    var dend by RegisterDelegate(REG_DEND, this.mem)

    /** Start of the CODE section. This comes from the memory configuration
     * passed in, and normally wouldn't ever change. However, to hack around,
     * users *can* change this.
     */
    var cstart by RegisterDelegate(REG_CSTART, this.mem)

    /** Start of the DATA section. See [cstart]. */
    var dstart by RegisterDelegate(REG_DSTART, this.mem)

    /** The pointer the scanner is at in a line of input. */
    var inPtr by RegisterDelegate(REG_IN_PTR, this.mem)

    // *************************************************************************

    /**  Word dictionary. */
    val dict = Dict(this)

    /**  This is only needed to make see/simple-see commands more helpful. */
    val cellMeta = Array(memConfig.upperBound + 1) { CellMeta.Unknown }

    /** Data stack (the one normally used by end users) */
    val dstk = FStack(this, "dstk", memConfig.dstackStart, memConfig.dstackEnd)

    /** Return stack (for calling/returning from fn calls */
    val rstk = FStack(this, "rstk", memConfig.rstackStart, memConfig.rstackEnd)

    /** Current word being executed by the VM. */
    lateinit var currentWord: Word

    /** The instruction pointer; where is the VM executing next? */
    var ip = memConfig.codeStart

    /** List of {name: class} for all primitive modules loaded. */
    val modulesLoaded: HashMap<String, IWordModule> = HashMap()

    /** List of files included. */
    val includedFiles: ArrayList<String> = ArrayList()

    /** Stack of input sources, with the top being the active one. */
    val sources = arrayListOf<SourceBase>()

    /** Convenient way to get the currently-active input source (or null)
     *
     * Not marking this a nullable, even though it can be for a moment when
     * the system is shutting down and pulling the last interpreter off,
     * or during bootstrapping when there's no interactive input source to
     * fall back on.
     *
     * Making this a nullable type would require all sorts of null-safety
     * checks everywhere that uses it, and it's fine to minimize the specific
     * locations in the codebase that can be reached when there's no input
     * source.
     * */
    val source: SourceBase get() = sources.last()

    /** Time mark for when VM started (the `millis` word reports # of millis
     * since server start, since it's not possible to return millis before the
     * 1970 epoch on a 32-bit machine.)
     */
    val timeMarkCreated = TimeSource.Monotonic.markNow()

    init {
        verbosity = initVerbosity
        io.setUp(this)
        interp.setUp(this)
    }


    // *************************************************************************

    /** Boot or reboot the machine. This should clear everything. */

    fun reboot(includePrimitives: Boolean = true) {
        if (D) dbg(2, "vm.reboot")
        if (verbosity > 0) io.info("Rebooting...")

        val curVerbosity = verbosity  // restore to current after reboot

        mem.fill(0)  // keep this above register setting, since it clears them
        cellMeta.fill(CellMeta.Unknown)

        base = 10
        verbosity = curVerbosity
        cstart = memConfig.codeStart
        cend = cstart
        dstart = memConfig.dataStart
        dend = memConfig.dataStart

        ip = cstart
        currentWord = Word.noWord

        dict.reset()
        modulesLoaded.clear()
        dict.addMetaModule(interp.module)
        if (includePrimitives) addCoreWords()

        sources.clear()
        sources.add(initSourceMaker(this))
        interp.reboot()
        quit()
    }

    /** Quit: what `QUIT` calls --- go to interactive interp and reset.
     *
     * The name "QUIT" is traditional in forth and tied to the QUIT word,
     * but it doesn't really mean "quit the VM" or "quit the interpreter" or
     * such. It means, basically, "quit reading any file or evaluating this
     * line and go back to interactive mode and read a new line."
     *
     * The word to quit the interpreter is BYE.
     * */

    fun quit() {
        if (D) dbg(2, "vm.quit")

        // Go to the interactive input, so peel off any inputs stacked on top
        // of this.
        while (source.id != 0) {
            source.pop()
            if (sources.isEmpty()) throw IntBye()
        }

        source.scanner.nextLine()
        rstk.reset()
        ip = cstart
        interp.reset()
    }

    /** Abort: this what `abort` does as well as any forth error */

    fun abort() {
        if (D) dbg(2, "vm.abort")

        quit()
        dstk.reset()
    }


    // ************************************************** Adding primitive words

    /**  Add all the words included in the software. */

    fun addCoreWords() {
        if (D) dbg(3, "vm.addCoreWords")

        // Keep this up-to-date with "what are the modules we want as default?"
        for (metaMod in arrayOf(
            mCore,
            mCoreExt,
            mDoubleNums,
            mFacility,
            mFileAccess,
            mTools,
            mCustom,
        )) {
            dict.addMetaModule(metaMod)
        }

        for (mod in arrayOf<IWordModule>(
            // none for now, but in case it's needed
        )) {
            dict.addModule(mod)
        }
    }


    // ************************************************************ VM core loop

    /**  Run the VM.
     *
     * The VM needs to be rebooted prior to this. This runs the VM until it is
     * forced to stop with an uncaught exception.
     *
     * Note that this isn't the "interpreter"; that's a *program the VM can
     * run* (and, unless you want a really bare-bones experience, you will want
     * to run).
     */
    fun runVM(): Nothing {
        if (D) dbg(3, "vm.run")

        while (true) {
            try {
                innerRunVM()
            } catch (e: ArrayIndexOutOfBoundsException) {
                io.danger("$source MEMFAULT Mem Out of bound: $ip")
                if (verbosity >= 3) io.muted(e.stackTraceToString())
                abort()
            } catch (e: ForthError) {
                io.danger("$source ERROR: " + e.message)
                if (verbosity >= 3) io.muted(e.stackTraceToString())

                // all Forth-errors call abort: clearing stacks, ignoring the
                // rest of the line and, if reading from a file or EVALUATING,
                // goes up to the interactive mode.
                abort()
            }
            // Note that not all errors are handled here --- the "IntXXXX"
            // exceptions are "interrupts" for the system, and are either
            // entirely uncaught (like IntBrk, which crashes the system)
            // or is caught higher up, to do things like shutdown politely.
        }
    }

    /** Run the VM until an error happens.
     *
     * This is separated out so things like tests can use it.
     *
     * That's useful so that errors aren't caught here.
     */

    fun innerRunVM(): Nothing {
        while (true) {
            // get the opcode to run, and find it in the dictionary
            val wn = mem[ip++]
            val w = dict[wn]
            // run it
            w(this)
        }
    }


    /** Programmers get lonely and we love to get logs. */
    @Suppress("KotlinConstantConditions")
    fun dbg(lvl: Int, s: String) {
        if (!D) throw Exception("FIX THIS: DBG called when D is false")
        when (lvl) {
            0, 1, 2 -> io.debug(s)
            else -> io.debugSubtle(s)
        }
    }

    companion object {
        const val REG_BASE = 0
        const val REG_VERBOSITY = 1
        const val REG_CSTART = 2
        const val REG_CEND = 3
        const val REG_DSTART = 4
        const val REG_DEND = 5
        const val REG_STATE = 7
        const val REG_IN_PTR = 8

        const val MAX_INT: Int = 0x7fffffff
        const val TRUE: Int = -1
        const val FALSE: Int = 0

        const val CHAR_SIZE: Int = 1 // apparently, we're unicode-32 ;-)
    }
}
