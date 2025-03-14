package kf


import com.github.ajalt.mordant.terminal.StandardTerminalInterface
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.danger
import com.github.ajalt.mordant.terminal.info
import com.github.ajalt.mordant.rendering.TextColors.gray
import com.github.ajalt.mordant.terminal.success
import com.github.ajalt.mordant.terminal.warning
import kf.primitives.WComments
import kf.primitives.WCompiling
import kf.primitives.WDoes
import kf.primitives.WDoubleNum
import kf.primitives.WFunctions
import kf.primitives.WIfThen
import kf.primitives.WInclude
import kf.primitives.WInputOutput
import kf.primitives.WInternals
import kf.primitives.WInterp
import kf.primitives.WLoops
import kf.primitives.WMachine
import kf.primitives.WMathLogic
import kf.primitives.WMemory
import kf.primitives.WMisc
import kf.primitives.WStackOps
import kf.primitives.WStrings
import kf.primitives.WTools
import kf.primitives.WWords
import kotlin.time.TimeSource

import kotlin.reflect.KProperty

// *****************************************************************************
// Some orientation:
//
// This file contains the actual VM as well as some parts that are needed
// by the interpreter loop (the VM can be used without an interpreter, or
// perhaps even with a different, all-in-Forth interpreter). For now, this is
// all one Java class, but this file is divided into sections, and the VM
// stuff is at the top, and the interpreter stuff is at the bottom.


/**
 *  A Forth virtual machine. This executes in Forth-memory code and
 *  manages Forth memory, registers, and stacks.
 */

class ForthVM(
    /** Terminal used by the VM */
    var io: Terminal = Terminal(),

    /** Memory layout of this VM. */
    val memConfig: IMemConfig = SmallMemConfig,

    /** RAM for the VM */
    val mem: IntArray = IntArray(memConfig.upperBound + 1),
) {

    /** Convenience for creating registers, which have getters/setters that
     * uses the underlying [mem].
     */
    inner class RegisterDelegate(val addr: Int) {
        operator fun getValue(thisRef: Any?, prop: KProperty<*>): Int {
            return mem[addr]
        }

        operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: Int) {
            mem[addr] = value
        }
    }


    // *************************************************************** registers
    /** Base of math output (10 is for decimal and default) */
    var base: Int by RegisterDelegate(REG_BASE)

    /** Verbosity of system:
     *     4 - very low-level debugging
     *     3 - show internal msgs
     *     2 - show all words
     *     1 - welcome messages, user warnings, etc (default)
     *     0 - no banner or unimportant warnings
     *     -1 - quiet: no warnings
     *     -2 - very no prompt, no output except direct (like .)
     *
     *  To see any dev-debugging messages, the global variable [D] needs to
     *  be true.
     */
    var verbosity: Int by RegisterDelegate(REG_VERBOSITY)

    /** Current end of the CODE section. */
    var cend: Int by RegisterDelegate(REG_CEND)

    /** Current end of the DATA section. */
    var dend: Int by RegisterDelegate(REG_DEND)

    /** Start of the CODE section. This comes from the memory configuration
     * passed in, and normally wouldn't ever change. However, to hack around,
     * users *can* change this.
     */
    var cstart: Int by RegisterDelegate(REG_CSTART)

    /** Start of the DATA section. See [cstart]. */
    var dstart: Int by RegisterDelegate(REG_DSTART)

    // *************************************************************************

    /**  Word dictionary. */
    val dict = Dict(this)

    /**  This is only needed to make see/simple-see commands more helpful. */
    val cellMeta = Array(memConfig.upperBound + 1) { CellMeta.Unknown }

    /** Data stack (the one normally used by end users) */
    val dstk = FStack(this, "dstk", memConfig.dstackStart, memConfig.dstackEnd)

    /** Return stack (for calling/returning from fn calls */
    val rstk = FStack(this, "rstk", memConfig.rstackStart, memConfig.rstackEnd)

    /** Loop stack (keeps track of i/j/k variables and loop nest depth */
    val lstk = FStack(this, "lstk", memConfig.lstackStart, memConfig.lstackEnd)

    /** Current word being executed by the VM. */
    lateinit var currentWord: Word  // TODO: this should be removed

    /** The instruction pointer; where is the VM executing next? */
    var ip = memConfig.codeStart

    /** List of {name: class} for all primitive modules loaded. */
    val modulesLoaded: HashMap<String, WordClass> = HashMap()

    /** Time mark for when VM started (the `millis` word reports # of millis
     * since server start, since it's not possible to return millis before the
     * 1970 epoch on a 32-bit machine.
     */
    val timeMarkCreated = TimeSource.Monotonic.markNow()

    init {
        // THere needs to be a verbosity set, so setting it to mildly-chatty.
        // Most callers will directly set this on their vm instance before
        // rebooting.
        this.verbosity = 1
    }

    // *************************************************************************

    /** Boot or reboot the machine. This should clear everything. */

    fun reboot(includePrimitives: Boolean = true) {
        if (D) dbg(1, "vm.reboot")
        if (verbosity > 0) io.info("Rebooting...")

        val curVerbosity = verbosity  // restore to current after reboot

        mem.fill(0)  // keep this above register setting, since it clears them
        cellMeta.fill(CellMeta.Unknown)

        base = 10
        verbosity = curVerbosity
        cstart = memConfig.codeStart
        cend = memConfig.codeStart
        dstart = memConfig.dataStart
        dend = memConfig.dataStart

        ip = memConfig.codeStart
        currentWord = Word.noWord

        dict.reset()
        dict.addModule(WMachine)
        dict.addModule(WInterp)
        if (includePrimitives) addCorePrimitives()

        rebootInterpreter()
        reset()
        if (verbosity > 0) banner()
    }

    /** Reset: this what `abort` does */

    fun reset() {
        if (D) dbg(1, "vm.reset")
        dstk.reset()
        rstk.reset()
        lstk.reset()
        ip = cstart

        resetInterpreter()
    }


    // ************************************************** Adding primitive words

    /**  Add a list of primitives at once, potentially printing new items as
     * added.
     */
    fun addCorePrimitives() {
        if (D) dbg(3, "vm.addCorePrimitives")

        for (mod in arrayOf(
            // Machine    // nt
            // Interp     // nt
            WInclude, // nt
//            WRegisters(this), // nt
            WTools, // nt
            WComments,
            WInputOutput,
            WStackOps, // nt
            WMathLogic,
            WMemory, // nt
            WFunctions,
            WCompiling,
            WIfThen,
            WLoops, // nt
            WDoes, // nt
            WMisc, // nt
            WInternals,
            WWords, // nt
            WStrings, // nt
            WDoubleNum, // nt
        )) dict.addModule(mod)
    }

    // ***************************************************** Adding to VM memory

    /**  Append word to the code section. This is just a convenience
     * function for "appendCode", as this can be passed the word name
     * and will safely check for it in the dictionary.
     */
    fun appendWord(s: String) {
        if (D) dbg(3, "vm.appendWord: $s")
        val wn = dict.getNum(s)
        appendCode(wn, CellMeta.WordNum)
    }

    /**  Append data to the code section.
     */
    fun appendCode(v: Int, cellMetaVal: CellMeta) {
        if (D) dbg(3, "vm.appendText: $v $cellMetaVal")
        if (cend > memConfig.codeEnd) throw ForthError("Code buffer overflow")

        mem[cend] = v
        cellMeta[cend] = cellMetaVal
        cend += 1
    }

    /**  Append string to the data section
     */
    fun appendStrToData(s: String): Int {
        if (D) dbg(3, "vm.appendStrToData: $s")
        val startAddr: Int = dend
        cellMeta[startAddr] = CellMeta.StringLit
        mem[dend++] = s.length

        for (c in s) mem[dend++] = c.code
        return startAddr + 1
    }

    /**  Append jump + loc to the code section
     */
    fun appendJump(s: String, addr: Int) {
        appendWord(s)
        appendCode(addr, CellMeta.JumpLoc)
    }

    /** Append "lit" + value to code section
     */
    fun appendLit(v: Int) {
        appendWord("lit")
        appendCode(v, CellMeta.NumLit)
    }


    // ************************************************************ VM core loop

    /**  Run the VM.
     *
     * The VM needs to be rebooted prior to this. This runs the VM until it is
     * forced to stop with one of several exceptions.
     *
     * Note that this isn't the "interpreter"; that's a *program the VM can
     * run* (and, unless you want a really bare-bones experience, you will want
     * to run).
     */
    fun runVM(): Nothing {
        if (D) dbg(3, "vm.run")

        while (true) {
            try {
                val wn = mem[ip++]
                dict[wn](this)
            } catch (e: ForthQuit) {
                // For non-interactive (like a file), needs to stop reading all
                // files --- so rethrow error
                if (io.terminalInterface !is StandardTerminalInterface) throw e
                // otherwise, it just resets call stack
                rstk.reset()
            } catch (e: ForthWarning) {
                io.warning("WARNING: " + e.message)
            } catch (e: ForthError) {
                // Any normal error will be ForthError (or a subclass of it);
                // other errors will continue upward. These will be
                // ForthBye (which is handled by the caller of this)
                // or any other unexpected programming errors.
                //
                // For ForthErrors, just show the user a message, reset
                // the machine (empty stacks, etc.), and let them continue.
                io.danger("ERROR: " + e.message)
                if (verbosity >= 2)
                    e.printStackTrace()
                reset()
            } catch (e: ForthBrk) {
                io.danger("BRK: " + e.message)
                e.printStackTrace()
            }
        }
    }


    // ************************************************************* interpreter
    //
    // While you'll almost certainly want an interpreter for Forth, it's a
    // separate layer of the code: the VM *could* run by itself using a
    // precompiled program poked directly into memory and executed.
    //
    // Everything from this point downward is the things that are needed for
    // the interpreter, but not needed for the VM itself.

    /** A register for interpreter use: state of interpreting/compiling. */
    var interpState: Int by RegisterDelegate(REG_INTERP_STATE)

    val isInterpretingState
        get() = mem[REG_INTERP_STATE] == INTERP_STATE_INTERPRETING
    val isCompilingState
        get() = mem[REG_INTERP_STATE] == INTERP_STATE_COMPILING


    /**  Buffer holding the most recently read token. */
    var interpToken: String = ""

    /**  Scanner for reading and tokenizing input line. */
    var interpScanner: FScanner = FScanner(
        this, memConfig.interpBufferStart, memConfig.interpBufferEnd)

    // ************************************************ Reboot/reset interpreter

    /**  Handle a VM reboot at the interpreter layer.
     */
    private fun rebootInterpreter() {
        if (D) dbg(3, "vm.rebootInterpreter")
        interpToken = ""
        interpScanner.reset()

        // Put interpreter code in mem; the VM will start executing here
        addInterpreterCode(cstart)
        resetInterpreter()
    }

    /**  Handle a VM reset at the interpreter layer.
     */
    private fun resetInterpreter() {
        if (D) dbg(3, "vm.resetInterpreter")

        // If error happens while defining word, roll back this word.
        dict.currentlyDefining?.let { w ->
            cend = w.cpos
            dict.removeLast()
            dict.currentlyDefining = null
        }
        interpState = INTERP_STATE_INTERPRETING
    }


    // ******************************************************* Interpreter modes

    /** Called by w_processToken when InterpretedMode is "compiling":
     *
     * Most parts in the definition and just added directly.
     * However, words that are "immediate-mode" will execute.
     */
    fun interpCompile(token: String) {
        if (D) dbg(3, "vm.interpCompile: $token")
        val w: Word? = dict.getSafeChkRecursion(token, io)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: ${w.name}")
            else if (w.imm) {
                w(this)
            } else {
                appendCode(w.wn, CellMeta.WordNum)
            }
        } else if (token.isCharLit) {
            appendLit(token[1].code)
        } else {
            val n: Int = token.toForthInt(base)
            appendWord("lit")
            appendCode(n, CellMeta.NumLit)
        }
    }

    /**  Called by w_processToken when Interpreter mode is "interpreting":
     *
     * Execute current token: if a word, run it; else, try as number.
     */

    fun interpInterpret(token: String) {
        if (D) dbg(3, "vm.interpInterpret: $token")
        val w: Word? = dict.getSafe(token)
        if (w != null) {
            if (w.compO) throw InvalidState("Compile-only: " + w.name)
            w(this)
        } else if (token.isCharLit) {
            dstk.push(token[1].code)
        } else {
            dstk.push(token.toForthInt(base))
        }
    }


    // *************************************************** the Forth interpreter

    /** Instructions for the VM for the Forth interpreter
     *
     * This is poked into memory during the reboot process; this is the
     * interpreter loop:
     *
     * - show prompt
     *
     * - read line of input (from terminal/file/wherever io tells us)
     * - if null, jump to EOF-point, below
     *
     * - read next token from input
     * - if null, jump back to show-prompt
     *
     * - call w_processToken
     *
     * - go back to read-next-token
     *
     * - EOF: we get here when no more input from io system
     * - execute w_eof, which throws an EOF error
     *
     * That's normally going to stop the interpreter. However, if the
     * interpreter is getting code from files, it might just move onto
     * the next file. Or, if a terminal-user uses "include ..." to read
     * from a file, after an EOF in that file, it will cede control back
     * to the console.
     *
     * However, in the general case, this ends the
     * session with the VM and the program ultimately stops.
     *
     * - In cases where the IO subsystem gets more input (another file
     * or returning from file-reading to the console user), the EOF
     * won't be fatal, so jump back the show-prompt top and continue.
     *
     * The w_processToken word right now is just a switch between calling
     * the Java code for interpExecute and interpCompile, but maybe one day
     * more of this will be done at the Forth level, allowing users to
     * customize their own interpreters more without less reliance on part of
     * that loop being locked up in non-word code: that would require exposing
     * more of the actual dictionary access to Forth for people to be able
     * to write more interpreter internals in Forth. */
    fun addInterpreterCode(startAddr: Int) {
        if (D) dbg(3, "vm.addInterpreterCode: $startAddr")

        appendWord("interp-prompt")
        appendWord("interp-refill")
        appendJump("0branch", startAddr + 10)
        appendWord("interp-read")
        appendJump("0branch", startAddr)
        appendWord("interp-process")
        appendJump("branch", startAddr + 4)
        appendWord("eof")
        appendJump("branch", startAddr + 8)
    }

    fun dbg(lvl: Int, s: String) {
        if (verbosity < lvl) return
        when (lvl) {
            0, 1, 2 -> io.info(s)
            else -> io.println(gray(s))
        }
    }

    fun banner() {
        io.success("\nWelcome to ${VERSION_STRING}\n")
    }

    fun getToken(): String {
        if (D) dbg(3, "getToken")
        val (addr, len) = interpScanner.parseName()
        interpToken = interpScanner.getAsString(addr, len)
        return interpToken
    }

    companion object {
        const val REG_BASE = 0
        const val REG_VERBOSITY = 1
        const val REG_CSTART = 2
        const val REG_CEND = 3
        const val REG_DSTART = 4
        const val REG_DEND = 5
        const val REG_INTERP_STATE = 7

        const val INTERP_STATE_INTERPRETING: Int = 0
        const val INTERP_STATE_COMPILING: Int = -1

        const val MAX_INT: Int = 0x7fffffff
        const val TRUE: Int = -1
        const val FALSE: Int = 0
    }
}
