package kf

import kf.Word.Companion.noWord
import java.util.*

const val D = true;
const val VERSION_STRING = "KPupForth 0.1.0"

enum class CellMeta {
    unknown,
    word_number,
    jump_location,

    //    reg_base,
//    reg_verbosity,
//    reg_cend,
//    reg_dend,
    string_literal,
    number_literal;

    fun getExplanation(vm: ForthVM, v: Int): String {
        val generalFormat: String = String.format(
            "%d $%x %s",
            v,
            v,
            if (v >= ' '.code && v <= '~'.code) "'" + v.toChar() + "'" else ""
        )
        return when (this) {
            word_number -> vm.dict.get(v).name
            jump_location -> kotlin.String.format("--> 0x%04x", v)
            CellMeta.unknown, CellMeta.number_literal -> generalFormat
            string_literal -> "$v (string length)"
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

class ForthVM(
    val regsStart: Int = 0x0000,
    val regsEnd: Int = 0x000f,
    val codeStart: Int = 0x0010,
    val codeEnd: Int = 0x01ff,
    val dataStart: Int = 0x0200,
    val dateEnd: Int = 0x02ff,
    val dstackStart: Int = 0x0300,
    val dstackEnd: Int = 0x03ff,
    val rstackStart: Int = 0x03e0,
    val rstackEnd: Int = 0x03ef,
    val lstackStart: Int = 0x03f0,
    val lstackEnd: Int = 0x03ff,
    val upperBound: Int = 0x03ff,
    var io: IOBase = IOBase(),
    val mem: IntArray = IntArray(upperBound + 1),
) {

    // *************************************************************** registers

    val REG_BASE = 0
    var base: Int
        get() = mem[REG_BASE]
        set(v: Int) {
            mem[REG_BASE] = v
        }

    val REG_VERBOSITY = 1
    var verbosity: Int
        get() = mem[REG_VERBOSITY]
        set(v: Int) {
            mem[REG_VERBOSITY] = v
        }

    val REG_CEND = 2
    var cend: Int
        get() = mem[REG_CEND]
        set(v: Int) {
            mem[REG_CEND] = v
        }

    val REG_DEND = 3
    var dend: Int
        get() = mem[REG_DEND]
        set(v: Int) {
            mem[REG_DEND] = v
        }

    val REG_TERM_WIDTH = 4
    var termWidth: Int
        get() = mem[REG_TERM_WIDTH]
        set(v: Int) {
            mem[REG_TERM_WIDTH] = v
        }


    val cellMeta: Array<CellMeta> =
        Array(upperBound + 1) { CellMeta.unknown }
    val dict: Dict = Dict(this)
    var currentWord: Word = Word.noWord
    var cptr: Int = codeStart
    val dstk = FStack(this, "dstk", dstackStart, dstackEnd)
    val rstk = FStack(this, "lstk", dstackStart, dstackEnd)
    val lstk = FStack(this, "rstk", dstackStart, dstackEnd)

    fun reboot(includePrimitives: Boolean = true) {
        if (D) dbg(1, "vm.reboot")
        // The only things we want to hold onto
        val curVerbosity: Int = verbosity
        val curTermWidth = if (termWidth == 0) 80 else termWidth


        // Clear memory
        for (i in 0..upperBound) mem[i] = 0
        for (i in 0..upperBound) cellMeta[i] = CellMeta.unknown

        dict.reset()
        for (i in mem.indices) mem[i] = 0

        cptr = codeStart
        cend = codeStart
        dend = dataStart
        currentWord = Word.noWord


        // Reset registers
        base = 10
//        cellMeta[ForthVM.REG_BASE] = CellMeta.reg_base
//        cellMeta[ForthVM.REG_VERBOSITY] = CellMeta.reg_verbosity
//        cellMeta[ForthVM.REG_CEND] = CellMeta.reg_cend
//        cellMeta[ForthVM.REG_DEND] = CellMeta.reg_dend

        dstk.reset()
        rstk.reset()
        lstk.reset()

        verbosity = curVerbosity
        termWidth = curTermWidth

        dict.addMany(WMachine(this).primitives, "Machine")
        dict.addMany(WInterp(this).primitives, "Machine")

        if (includePrimitives) addCorePrimitives()

        // Do things at the interpreter level that are needed after a reboot
        rebootInterpreter();
        addInterpreterCode(codeStart)

        if (verbosity > 0) {
            io.output.println(io.green("\nWelcome to ${VERSION_STRING}\n"))
        }

    }

    fun reset() {
        if (D) dbg(1, "vm.reset");
        dstk.reset()
        rstk.reset()
        lstk.reset()
        cptr = codeStart


        // Do things at the interpreter level that are need after a reset
        resetInterpreter();
    }


    // ************************************************** Adding primitive words
    /**  Add a list of primitives at once, potentially printing new items as
     * added.
     */
    fun addCorePrimitives() {
        if (D) dbg(3, "vm.addCorePrimitives")
        dict.addMany(WTools(this).primitives, "Tools")
        dict.addMany(WComments(this).primitives, "Comments")
        dict.addMany(WInputOutput(this).primitives, "InputOutput")
        dict.addMany(WStackOps(this).primitives, "StackOps")
        dict.addMany(WMathLogic(this).primitives, "MathLogic")
        dict.addMany(WMemory(this).primitives, "Memory")
        dict.addMany(WFunctions(this).primitives, "Functions")
        dict.addMany(WCompiling(this).primitives, "Compiling")
        // dep: Functions
        dict.addMany(WIfThen(this).primitives, "IfThen") // dep: Functions
//        dict.addMany(Looping.primitives, "Looping") // dep: Functions
        dict.addMany(WDoes(this).primitives, "Does") // dep: Functions
        dict.addMany(WMisc(this).primitives, "Misc") // dep: Functions
        dict.addMany(WInternals(this).primitives, "Internals")
//        dict.addMany(PrimWords.primitives, "Words")
//        dict.addMany(Strings.primitives, "Strings")
    }

    /**  Read in a primitive class dynamically
     */
    fun readPrimitiveClass(name: String) {
        if (D) dbg(3, "vm.readPrimitiveClass: %s", name)
        try {
            val cls = Class.forName(name)
            val field = cls.getDeclaredField("primitives")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            dict.addMany(field[null] as Array<Word>, name)
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
        if (D) dbg(3, "vm.appendWord: %s", s)
        val wn = dict.getNum(s)
        appendCode(wn, CellMeta.word_number)
    }

    /**  Append data to the code section.
     */
    fun appendCode(v: Int, cellMeta_val: CellMeta) {
        if (D) dbg(3, "vm.appendText: ${v} ${cellMeta_val}")
        if (cend > codeEnd) throw ForthError("Code buffer overflow")

        mem[cend] = v
        cellMeta[cend] = cellMeta_val
        cend += 1
    }

    /**  Append string to the data section
     */
    fun appendStrToData(s: String): Int {
        if (D) dbg(3, "vm.appendStrToData: ${s}")
        val startAddr: Int = dend
        cellMeta[startAddr] = CellMeta.string_literal
        mem[dend++] = s.length

        for (c in s.toCharArray()) mem[dend++] = c.code
        return startAddr + 1
    }

    /**  Append jump + loc to the code section
     */
    fun appendJump(s: String, addr: Int) {
        appendWord(s)
        appendCode(addr, CellMeta.jump_location)
    }

    /** Append "lit" + value to code section
     */
    fun appendLit(v: Int) {
        appendWord("lit")
        appendCode(v, CellMeta.number_literal)
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
    fun runVM() {
        if (D) dbg(3, "vm.run")

        while (true) {
            val wn = mem[cptr++]
            currentWord = dict.get(wn)
            currentWord.callable.invoke(this)
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
    val REG_INTERP_STATE: Int = 0x0f
    val INTERP_STATE_INTERPRETING: Int = 0
    val INTERP_STATE_COMPILING: Int = -1

    var interpState: Int
        get() = mem[REG_INTERP_STATE]
        set(v) {
            mem[REG_INTERP_STATE] = v
        }
    val isInterpretingState: Boolean
        get() = mem[REG_INTERP_STATE] == INTERP_STATE_INTERPRETING
    val isCompilingState: Boolean
        get() = mem[REG_INTERP_STATE] == INTERP_STATE_COMPILING


    /**  End-of-file detected (exists current interpreter, both interactive and
     * non-interactive)
     */
    class ForthEOF : java.lang.RuntimeException()

    /**  When raised, will quit the interpreter *if* this is a non-interactive
     * interpreter (like when reading a file with `include`)
     */
    class ForthQuitNonInteractive : java.lang.RuntimeException()

    /**  Might be an invalid number or missing word.
     */
    class ParseError(message: String) : ForthError("Parse error: $message")

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
        currentWord = noWord
        interpScanner = null // it will make one once it gets some input
        interpLineBuf = ""

        // Poke interpreter code into memory: the VM will start executing
        // code at this location (mem_code_start)
        addInterpreterCode(codeStart)
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
        if (D) dbg(3, "vm.compile: %s", token)
        val w: Word? = dict.getSafeChkRecursion(token, io)

        if (w != null) {
            if (w.interpOnly)
                throw ForthError("Can't use in compile mode: ${w.name}")
            if (w.immediate) {
                currentWord = w
                w.exec(this)
            } else {
                appendCode(w.wn!!, CellMeta.word_number)
            }
            return
        }

        if (token.startsWith("'")) {
            if (token.length == 2 || (token.length == 3 && token[2] == '\'')) {
                appendLit(token[1].code)
                return
            }
        }

        val n: Int = tryAsInt(token, base)
        appendWord("lit")
        appendCode(n, CellMeta.number_literal)
    }

    /**  Called by w_processToken when Interpreter mode is "interpreting":
     *
     * Execute current token: if a word, run it; else, try as number.
     */
    fun interpInterpret(token: String) {
        if (D) dbg(3, "vm.execute: %s", token)
        val w: Word? = dict.getSafe(token)
        if (w != null) {
            if (w.compileOnly) throw ForthError("Compile-only: " + w.name)
            currentWord = w
            w.exec(this)
            return
        }
        if (token.startsWith("'")) {
            if (token.length == 2
                || (token.length == 3 && token[2] == '\'')
            ) {
                dstk.push(token[1].code)
                return
            }
        }
        dstk.push(tryAsInt(token, base))
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
        if (D) dbg(3, "vm.addInterpreterCode: ${startAddr}")

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

    fun testProgram() {
        appendLit(10)
        appendLit(20)
        appendWord("+")
        appendWord(".")
        appendWord("brk")
    }


    fun dbg(lvl: Int, format: String, vararg args: Any?) {
        if (verbosity > lvl) return
        when (lvl) {
            0, 1, 2 -> io.output.printf(io.yellow(format + "\n"), *args)
            else -> io.output.printf(io.grey(format + "\n"), *args)
        }
    }

    fun dbg(format: String, vararg args: Any?) = dbg(2, format, *args)


    fun getToken(): String {
        if (D) dbg(3, "getToken")
        if (interpScanner != null && interpScanner!!.hasNext()) {
            interpToken = interpScanner!!.next()
            return interpToken!!
        } else {
            interpToken = ""
            throw ForthMissingToken()
        }
    }
}
