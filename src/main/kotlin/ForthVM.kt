package kf


import RegisterDelegate
import kf.interps.IInterp
import kf.words.core.ext.mCoreExt
import kf.words.core.mCore
import kf.words.custom.mCustom
import kf.words.doublenums.mDoubleNums
import kf.words.facility.mFacility
import kf.words.fileaccess.mFileAccess
import kf.words.tools.mTools
import org.jline.reader.LineReader
import org.jline.terminal.TerminalBuilder
import kotlin.time.TimeSource


/**
 *  A Forth virtual machine.
 *
 *  This executes in Forth-memory code and
 *  manages Forth memory, registers, and stacks.
 *
 *  @property io Terminal used by VM. Defaults to std-out, detect-colors, etc.
 *  @property memConfig Memory layout of this VM.
 *  @property mem RAM for the VM
 */

class ForthVM(
//    val terminal = TerminalBuilder.builder().dumb(true).build(),
    var io: IOutputSource = TerminalOutputSource(),
    val memConfig: IMemConfig = SmallMemConfig(),
    val mem: IntArray = IntArray(memConfig.upperBound + 1),
) {


    /** Which interpreter is active?
     *
     * The VM needs to point to the interp, and the interp needs to point to the
     * VM, so this is a lateinit--the creator of the VM will patch the interp
     * on here after making it.
     */

    lateinit var interp: IInterp

    // *************************************************************** registers

    /** Base of math output (10 is for decimal and default) */
    var base by RegisterDelegate(REG_BASE, this.mem)

    /** Verbosity of system:
     *
     * -  4  very low-level debugging
     * -  3  show internal msgs
     * -  2  show all words
     * -  1  welcome messages, user warnings, etc (default)
     * -  0  no banner or unimportant warnings
     * - -1  quiet: no warnings
     * - -2  very no prompt, no output except direct (like .)
     *
     *  To see any dev-debugging messages, the global variable [D] needs to
     *  be true.
     */
    var verbosity by RegisterDelegate(REG_VERBOSITY, this.mem)

    /** Current end of the CODE section. */
    var cend  by RegisterDelegate(REG_CEND, this.mem)

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
    lateinit var currentWord: Word  // TODO: this should be removed

    /** The instruction pointer; where is the VM executing next? */
    var ip = memConfig.codeStart

    /** List of {name: class} for all primitive modules loaded. */
    val modulesLoaded: HashMap<String, IWordModule> = HashMap()

    /** List of files included. */
    val includedFiles: ArrayList<String> = ArrayList()

    /** Stack of input sources, with the top being the active one. */
    val sources = arrayListOf<InputSource>()

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
    val source: InputSource get() = sources.last()

    /** Time mark for when VM started (the `millis` word reports # of millis
     * since server start, since it's not possible to return millis before the
     * 1970 epoch on a 32-bit machine.
     */
    val timeMarkCreated = TimeSource.Monotonic.markNow()

    var readerForHistory: LineReader? = null

    // *************************************************************************

    /** Boot or reboot the machine. This should clear everything. */

    fun reboot(includePrimitives: Boolean = true) {
        if (D) dbg(2, "vm.reboot")
        if (verbosity > 0) io.info("Rebooting...")

        val curVerbosity = verbosity  // restore to current after reboot

        readerForHistory?.history?.save()
        sources.clear()
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

        interp.reboot()
        val inputSource = StdInInputSource(this)
        readerForHistory = inputSource.reader
        sources.add(inputSource)
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

        // Keep this up-to-date with "what are the modules we want as default?
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

    // ***************************************************** Adding to VM memory

    /**  Append to the code section.
     *
     * All the other "appendXXXX" names call this.
     * */
    fun appendCode(v: Int, cellMetaVal: CellMeta) {
        if (D) dbg(4, "vm.appendText: $v $cellMetaVal")
        if (cend > memConfig.codeEnd) throw MemError("Code buffer overflow")

        mem[cend] = v
        cellMeta[cend] = cellMetaVal
        cend += 1
    }

    /**  Append word to the code section.
     *
     * This is just a convenience function for "appendCode", as this can be
     * passed the word name and it will find the wn and add the meta info.
     */
    fun appendWord(s: String) {
        if (D) dbg(4, "vm.appendWord: $s")
        val wn = dict[s].wn
        appendCode(wn, CellMeta.WordNum)
    }

    /**  Append lit string ("lit" + len + chars) to the code section */
    fun appendStr(s: String) {
        if (D) dbg(3, "vm.appendStr: $s")

        appendWord("lit-string")
        appendCode(s.length, CellMeta.StringLen)
        for (c in s) mem[cend++] = c.code
    }

    /**  Append lit counted string ("lit" + chars) to the code section */
    fun appendCStr(s: String) {
        if (D) dbg(3, "vm.appendCStr: $s")

        appendWord("lit-string")
        // fixme: I don't think this will work; we're missing the count!
        for (c in s) mem[cend++] = c.code
    }

    /**  Append jump + loc to the code section */
    fun appendJump(s: String, addr: Int) {
        appendWord(s)
        appendCode(addr, CellMeta.JumpLoc)
    }

    /** Append "lit" + value to code section */
    fun appendLit(v: Int) {
        appendWord("lit")
        appendCode(v, CellMeta.NumLit)
    }

    // ************************************************* adding to data section

    /**  Append string to the data section
     *
     * This is just the string; unlike adding to CODE, there's no LIT-STRING
     * preceding it, since it won't be executed.
     **/
    fun appendStrToData(s: String): Int {
        if (D) dbg(3, "vm.appendStrToData: $s")
        val startAddr: Int = dend
        cellMeta[startAddr] = CellMeta.StringLen
        mem[dend++] = s.length

        for (c in s) mem[dend++] = c.code
        return startAddr + 1
    }

    /** Append counted string to the data section.
     *
     * Same thing (all strings are stored "counted"), but this returns the
     * address of the counted-string (ie, len+chars), rather than the addr of
     * the chars.
     * */
    fun appendCStrToData(s: String): Int {
        return appendStrToData(s) - 1
    }

    // ************************************************************ VM core loop

    /**  Run the VM.
     *
     * The VM needs to be rebooted prior to this. This runs the VM until it is
     * forced to stop with an exception.
     *
     * Note that this isn't the "interpreter"; that's a *program the VM can
     * run* (and, unless you want a really bare-bones experience, you will want
     * to run).
     */
    fun runVM(): Nothing {
        if (D) dbg(3, "vm.run")

        while (true) {
            try {
                // get the opcode to run, and find it in the dictionary
                val wn = mem[ip++]
                val w = dict[wn]
                // run it
                w(this)
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


    /** Programmers get lonely and we love to get logs. */
    fun dbg(lvl: Int, s: String) {
        if (verbosity < lvl) return
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
