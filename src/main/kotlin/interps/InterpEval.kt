package kf.interps

import com.github.ajalt.mordant.terminal.warning
import kf.*
import kf.words.core.ext.wInterpExt.w_parseName
import kf.words.custom.wToolsCustom
import kf.words.mEvalInterp

/** Base class for interpreters that don't have a CLI, but can evaluate
 * and compile. */

open class InterpEval(vm: ForthVM) : InterpBase(vm) {
    override val name = "Eval"
    override val module: IMetaWordModule = mEvalInterp
    // This isn't a real interpreter; just a test program that shows that this
    // can succeed at compiling the interpreter with the mini-interpreter.
    // Since there's nothing after this, expect it to break:
    open val code = """
        10 20 (.) (.) (WORDS)
    """

    /** Process a token: either compile or interpret it.
     *
     * This is what makes this interpreter "fast" --- the other interpreter
     * does this in Forth, which adds a dozen words to the interpreter loop.
     * By doing this in Kotlin, it speeds up the interpreter loop.
     *
     * (The speed isn't as helpful as the fact that the dev logging doesn't
     * contain so many words being executed, making it harder to see the
     * "real" code)
     *
     */

    fun w_processToken(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        var token = Pair(addr, len).strFromAddrLen(vm)
        if (D) vm.dbg(3, "vm.w_processToken")
        if (isInterpreting) interpret(token)
        else compile(token)
    }

    override fun reboot() {
        if (D) vm.dbg(3, "InterpEval.rebootInterpreter")

        // In order to compile the interpreter, we need access to this
        // internal and non-standard word. Make sure it's added to the dict,
        // and hide it from users.
        vm.dict.add(
            Word("PARSE-NAME", ::w_parseName, hidden = true)
        )
        vm.dict.add(
            Word("BOOTSTRAP-PROCESS-TOKEN", ::w_processToken, hidden = true)
        )
        super.reboot()
    }

    override fun reset() {
        if (D) vm.dbg(3, "InterpEval.resetInterpreter")

        // If error happens while defining word, roll back this word.
        vm.dict.currentlyDefining?.let { w ->
            vm.cend = w.cpos
            vm.dict.removeLast()
            vm.dict.currentlyDefining = null
        }
        state = STATE_INTERPRETING
    }

    /** Compile a token.
     *
     * This is the real compiler, so if the word for that token is immediate,
     * it will be executed. Else, it will be appended to the CODE section.
     *
     * Called by w_processToken when InterpretedMode is "compiling":
     *
     */

    override fun compile(token: String) {
        if (D) vm.dbg(3, "vm.interpCompile: $token")
        val w: Word? = vm.dict.getSafeChkRecursion(token)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: $w")
            else if (w.imm) {
                w(vm)
            } else {
                vm.appendCode(w.wn, CellMeta.WordNum)
            }
        } else {
            vm.appendLit(token.toForthInt(vm.base))
        }
    }

    /** Interpret a token. */

    override fun interpret(token: String) {
        if (D) vm.dbg(3, "vm.interpInterpret: $token")
        val w: Word? = vm.dict.getSafe(token)

        if (w != null) {
            if (w.compO) throw InvalidState("Compile-only: $w")
            w(vm)
        } else {
            vm.dstk.push(token.toForthInt(vm.base))
        }
    }

    /** Evaluate a line: simple one-line for bootstrapping. */

    override fun eval(line: String) {
        vm.scanner.fill(line)
        try {
            while (true) {
                val wn = vm.mem[vm.ip++]
                val w = vm.dict[wn]
                w(vm)
            }
        } catch (e: ForthInterrupt) {
            when (e) {
                is IntEOF -> return
                else -> throw e
            }
        }
    }

    /** Evaluate a line during bootstrapping.
     *
     * This is during the bootstrapping; it doesn't use the "real" interpreter;
     * this is needed to compile the real interpreter and put it at the start
     * of codeStart.
     *
     * */

     fun bootstrapEval(line: String) {
        // the "mini-interpreter" is located in scratch space here
        vm.ip = vm.memConfig.scratchStart
        vm.scanner.fill(line)
        try {
            while (true) {
                val wn = vm.mem[vm.ip++]
                val w = vm.dict[wn]
                w(vm)
            }
        } catch (e: ForthInterrupt) {
            when (e) {
                is IntEOF -> return
                else -> throw e
            }
        }
        vm.ip = vm.memConfig.codeStart
    }

    /** Set up bootstrap evaluator. */

    fun setUpBootstrapEval() {
        // Poke in the mini-interpreter at the very top of code mem
        vm.cend = vm.memConfig.scratchStart

        vm.appendWord("PARSE-NAME")
        vm.appendWord("DUP")
        vm.appendJump("0BRANCH", 4) // to 2drop
        vm.appendWord("BOOTSTRAP-PROCESS-TOKEN")
        vm.appendJump("BRANCH", -6)
        vm.appendWord("2DROP")
        vm.appendWord("EOF")

        vm.cend = vm.memConfig.codeStart
    }

    /** Compiles the "real" interpreter (depends on which subclass this is).
     *
     * Uses the mini-interpreter to compile the real one.
     */

    override fun addInterpreterCode() {
        if (D) vm.dbg(3, "vm.addInterpreterCode")

        setUpBootstrapEval()
        state = STATE_COMPILING

        code.split("\n")
            .forEach { bootstrapEval(it) }
            .also {
                if (D && vm.verbosity > 2) {
                    vm.io.warning("\nInterpreter is:")
                    wToolsCustom.w_dotCode(vm)
                    vm.io.println()
                }
            }
    }
}