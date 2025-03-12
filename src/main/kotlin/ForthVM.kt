package kf

import java.util.*
import kotlin.collections.HashMap
import kotlin.time.TimeSource

const val D = true
const val VERSION_STRING = "KPupForth 0.1.0"

@Suppress("KotlinConstantConditions")
val Int.charRepr get() =
    if (this in ' '.code..'~'.code) "'${this.toChar()}'" else ""


enum class CellMeta {
    Unknown,
    WordNum,
    JumpLoc,

    //    reg_base,
//    reg_verbosity,
//    reg_cend,
//    reg_dend,
    StringLit,
    NumLit;

    fun getExplanation(vm: ForthVM, v: Int): String {
        return when (this) {
            WordNum -> vm.dict[v].name
            JumpLoc -> "  --> ${v.addr}"
            Unknown, NumLit -> "$v ${v.hex} ${v.charRepr}"
            StringLit -> "$v (string length)"
//        CellMeta.reg_base -> generalFormat+" (reg: base)"
//        CellMeta.reg_verbosity -> generalFormat+" (reg: verbosity)"
//        CellMeta.reg_cend -> generalFormat+" (reg: cend)"
//        CellMeta.reg_dend -> generalFormat+" (reg: dend)"
        }
    }
}


open class ForthError(msg: String) : Exception(msg)
class ForthMissingToken() : ForthError("Missing token")
class ForthWarning(msg: String) : ForthError(msg)

class ForthBrk(msg: String) : RuntimeException(msg)
class ForthQuit(msg: String) : RuntimeException(msg)
class ForthBye(msg: String) : RuntimeException(msg)
class ForthColdStop(msg: String) : RuntimeException(msg)

/**  End-of-file detected (exists current interpreter, both interactive and
 * non-interactive)
 */
class ForthEOF : RuntimeException()

/**  When raised, will quit the interpreter *if* this is a non-interactive
 * interpreter (like when reading a file with `include`)
 */
class ForthQuitNonInteractive : RuntimeException()

/**  Might be an invalid number or missing word.
 */
class ParseError(message: String) : ForthError("Parse error: $message")

class InvalidState(message: String) : ForthError("Invalid state: $message")

class ForthVM(
    val memConfig: IMemConfig = SmallMemConfig,
    var io: IOBase = IOTerminal(),
    val mem: IntArray = IntArray(memConfig.upperBound + 1),
) {

    // *************************************************************** registers

    // TODO: maybe these could be delegates?
    //
    // var base: Int by registerDelegate(REG_BASE)

    var base
        get() = mem[REG_BASE]
        set(v) {
            mem[REG_BASE] = v
        }

    var verbosity
        get() = mem[REG_VERBOSITY]
        set(v) {
            mem[REG_VERBOSITY] = v
        }

    var cend
        get() = mem[REG_CEND]
        set(v) {
            mem[REG_CEND] = v
        }

    var dend
        get() = mem[REG_DEND]
        set(v) {
            mem[REG_DEND] = v
        }

    var termWidth
        get() = mem[REG_TERM_WIDTH]
        set(v) {
            mem[REG_TERM_WIDTH] = v
        }

    var cstart
        get() = mem[REG_CSTART]
        set(v) {
            mem[REG_CSTART] = v
        }

    var dstart
        get() = mem[REG_DSTART]
        set(v) {
            mem[REG_DSTART] = v
        }

    val cellMeta = Array(memConfig.upperBound + 1) { CellMeta.Unknown }
    val dict = Dict(this)
    lateinit var currentWord: Word
    var ip = memConfig.codeStart
    val dstk = FStack(this, "dstk", memConfig.dstackStart, memConfig.dstackEnd)
    val rstk = FStack(this, "rstk", memConfig.rstackStart, memConfig.rstackEnd)
    val lstk = FStack(this, "lstk", memConfig.lstackStart, memConfig.lstackEnd)
    val modulesLoaded: HashMap<String, WordClass> = HashMap()
    val timeMarkCreated = TimeSource.Monotonic.markNow()

    fun reboot(includePrimitives: Boolean = true) {
        if (D) dbg(1, "vm.reboot")
        // The only things we want to hold onto
        val curVerbosity: Int = verbosity
        val curTermWidth = if (termWidth == 0) 80 else termWidth

        // Clear memory
        mem.fill(0)
        cellMeta.fill(CellMeta.Unknown)

        dict.reset()
        for (i in mem.indices) mem[i] = 0

        // Reset registers
        cstart = memConfig.codeStart
        cend = memConfig.codeStart
        dstart = memConfig.dataStart
        dend = memConfig.dataStart
        termWidth = curTermWidth
        base = 10
        verbosity = curVerbosity

        ip = memConfig.codeStart
        currentWord = Word.noWord

//        cellMeta[ForthVM.REG_BASE] = CellMeta.reg_base
//        cellMeta[ForthVM.REG_VERBOSITY] = CellMeta.reg_verbosity
//        cellMeta[ForthVM.REG_CEND] = CellMeta.reg_cend
//        cellMeta[ForthVM.REG_DEND] = CellMeta.reg_dend

        dstk.reset()
        rstk.reset()
        lstk.reset()

        dict.addModule(WMachine(this))
        dict.addModule(WInterp(this))

        if (includePrimitives) addCorePrimitives()

        // Do things at the interpreter level that are needed after a reboot
        rebootInterpreter()
        addInterpreterCode(memConfig.codeStart)

        if (verbosity > 0) {
            io.o.println(io.green("\nWelcome to ${VERSION_STRING}\n"))
        }
    }

    fun reset() {
        if (D) dbg(1, "vm.reset")
        dstk.reset()
        rstk.reset()
        lstk.reset()
        ip = cstart


        // Do things at the interpreter level that are need after a reset
        resetInterpreter()
    }


    // ************************************************** Adding primitive words
    /**  Add a list of primitives at once, potentially printing new items as
     * added.
     */
    fun addCorePrimitives() {
        if (D) dbg(3, "vm.addCorePrimitives")

        for (mod in arrayOf(
            WRegisters(this),
            WTools(this),
            WComments(this),
            WInputOutput(this),
            WStackOps(this),
            WMathLogic(this),
            WMemory(this),
            WFunctions(this),
            WCompiling(this),
            WIfThen(this), // dep: Functions
            WLoops(this), // dep: Functions
            WDoes(this), // dep: Functions
            WMisc(this), // dep: Functions
            WInternals(this),
            WWords(this),
//            WStrings(this),
        )) dict.addModule(mod)
    }

    /**  Read in a primitive class dynamically
     */
    fun readPrimitiveClass(name: String) {
        if (D) dbg(3, "vm.readPrimitiveClass: $name")
        try {
            val cls: Class<*> = Class.forName(name)
            val mod = cls.getConstructor(ForthVM::class.java)
                .newInstance(this) as WordClass
            dict.addModule(mod)
        } catch (e: Exception) {
            when (e) {
                is ClassNotFoundException,
                is IllegalAccessException,
                is NoSuchFieldException ->
                    throw ForthError("Can't load: $name (${e.message})")

                else -> throw e // Re-throw other unexpected exceptions
            }
        }
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
                if (!io.isInteractive) throw e
                // otherwise, it just resets call stack
                rstk.reset()
            } catch (e: ForthWarning) {
                io.warn("WARNING: " + e.message)
            } catch (e: ForthError) {
                // Any normal error will be ForthError (or a subclass of it);
                // other errors will continue upward. These will be
                // ForthBye (which is handled by the caller of this)
                // or any other unexpected programming errors.
                //
                // For ForthErrors, just show the user a message, reset
                // the machine (empty stacks, etc.), and let them continue.
                io.error("ERROR: " + e.message)
                if (verbosity >= 1)
                    e.printStackTrace()
                reset()
            } catch (e: ForthBrk) {
                io.error("BRK: " + e.message)
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


    var interpState: Int
        get() = mem[REG_INTERP_STATE]
        set(v) {
            mem[REG_INTERP_STATE] = v
        }
    val isInterpretingState: Boolean
        get() = mem[REG_INTERP_STATE] == INTERP_STATE_INTERPRETING
    val isCompilingState: Boolean
        get() = mem[REG_INTERP_STATE] == INTERP_STATE_COMPILING


    /**  Buffer holding the most recently read token. */
    var interpToken: String? = null

    /**  Scanner for reading and tokenizing input line. */
    var interpScanner: Scanner? = null

    /**  Current input line read from input device. */
    var interpLineBuf: String? = null


    // ************************************************ Reboot/reset interpreter

    /**  Handle a VM reboot at the interpreter layer.
     */
    private fun rebootInterpreter() {
        if (D) dbg(3, "vm.rebootInterpreter")
        interpToken = ""
        interpState = INTERP_STATE_INTERPRETING
        currentWord = Word.noWord
        interpScanner = null // it will make one once it gets some input
        interpLineBuf = ""

        // Poke interpreter code into memory: the VM will start executing
        // code at this location (mem_code_start)
        addInterpreterCode(cstart)
    }

    /**  Handle a VM reset at the interpreter layer.
     */
    private fun resetInterpreter() {
        if (D) dbg(3, "vm.resetInterpreter")
        dict.currentlyDefining?.let {
            // If error happens while defining word, roll back this word.
            cend = it.cpos
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
        if (D) dbg(3, "vm.compile: $token")
        val w: Word? = dict.getSafeChkRecursion(token, io)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: ${w.name}")
            else if (w.imm) {
                w(this)
            } else {
                appendCode(w.wn, CellMeta.WordNum)
            }
        } else if ((token[0] == '\'')
            && (token.length == 2 || (token.length == 3 && token[2] == '\''))
        ) {
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
        if (D) dbg(3, "vm.execute: $token")
        val w: Word? = dict.getSafe(token)
        if (w != null) {
            if (w.compO) throw InvalidState("Compile-only: " + w.name)
            w(this)
        } else if ((token[0] == '\'')
            && (token.length == 2 || (token.length == 3 && token[2] == '\''))
        ) {
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
            0, 1, 2 -> io.o.println(io.yellow(s))
            else -> io.o.printf(io.grey(s))
        }
    }

    fun getToken(): String {
        if (D) dbg(3, "getToken")
        interpToken = interpScanner?.takeIf { sc -> sc.hasNext() }?.next()
            ?: throw ForthMissingToken()
        return interpToken!!
    }

    companion object {
        const val REG_BASE = 0
        const val REG_VERBOSITY = 1
        const val REG_CSTART = 2
        const val REG_CEND = 3
        const val REG_DSTART = 4
        const val REG_DEND = 5
        const val REG_TERM_WIDTH = 6
        const val REG_INTERP_STATE = 7

        const val INTERP_STATE_INTERPRETING: Int = 0
        const val INTERP_STATE_COMPILING: Int = -1

        const val MAX_INT: Int = 0x7fffffff
        const val TRUE: Int = -1
        const val FALSE: Int = 0
    }
}
