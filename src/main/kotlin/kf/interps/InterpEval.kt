package kf.interps

import kf.*
import kf.interfaces.IWordMetaModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.mem.*
import kf.words.core.ext.wInterpExt.w_parseName
import kf.words.custom.wToolsCustom
import kf.words.mEvalInterp

/** An interpreter that can compile/evaluate, but without a REPL.
 *
 * This can compile and evaluate tokens. It doesn't start up a REPL, though,
 * so it should be seeded with starting code.
 *
 * This is the parent class of the "real interpreters" (currently,
 * Fast and Forth).
 *
 * ## Bootstrapping
 *
 * a ForthVM needs *something* to run when it starts up. In earlier versions
 * of this project, it just POKEd in the interpreter directly from the
 * raw opcodes. That's fine and worked great, and a saner approach would be
 * to still do that.
 *
 * However, I want to make it easier to write other fancier or more interesting
 * interpreters without having to write and maintain their raw-code so they
 * can be poked in.
 *
 * So, instead, all interpreters at this level and higher *compile their
 * own interpreters from Forth source at startup*. So this pokes a super-tiny
 * minimal mini-interpreter (no REPL, but it can eval/compile a single line
 * of Forth). This class and its subclasses have the source for their
 * interpreter program in the [InterpEval.code] attribute. The mini-interpreter
 * compiles that and then finishes the bootstrapping process by starting
 * the execution of it.
 *
 * After that process is done, there is no future use for the mini-interpreter,
 * and it will get written over since it's in scratch space.
 *
 * This is somewhat similar to how Forth really worked in the real world
 * historical cases: rather than writing a totally-from-scratch Forth for
 * a new system, you'd make a very minimal "Planck-style" Forth and use that
 * to bootstrap the system.
 *
 * Good times.
 *
 * */


open class InterpEval() : InterpBase() {
    override val name = "Eval"
    override val module: IWordMetaModule = mEvalInterp

    // This isn't a real interpreter; just a test program that shows that this
    // can succeed at compiling the interpreter with the mini-interpreter.
    open val code = """
        10 20 (.) (.) (WORDS) BRK
    """

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

        super.reset()

        // If error happens while defining word, roll back this word.
        vm.dict.currentlyDefining?.let { w ->
            vm.cend = w.cpos
            vm.dict.removeLast()
            vm.dict.currentlyDefining = null
        }
    }

    /** Process a token: either compile or interpret it.
     *
     * This is a word used by the interpreter-program.
     *
     * In the higher-level interpreters, this will be used by their REPLs.
     * It is needed here just for bootstrapping (see above).
     * */

    fun w_processToken(vm: IForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        var token = Pair(addr, len).strFromAddrLen(vm as ForthVM)
        if (D) vm.dbg(3, "vm.w_processToken: $token")
        if (isInterpreting) interpret(token)
        else compile(token)
    }

    /** Compile a token.
     *
     * This is the real compiler, so if the word for that token is immediate,
     * it will be executed. Else, it will be appended to the CODE section.
     *
     * Called by w_processToken when InterpretedMode is "compiling":
     */

    override fun compile(token: String) {
        if (D) vm.dbg(3, "interp.compile: $token")
        val w: Word? = vm.dict.getSafeChkRecursion(token)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: $w")
            else if (w.imm) {
                vm.currentWord = w
                w.fn(vm)
            } else {
                vm.appendCode(w.wn, CellMeta.WordNum)
            }
        } else {
            vm.appendLit(token.toForthInt(vm.base))
        }
    }

    /** Interpret a token.
     *
     * This is the real interpreter. Called by w_processToken when
     * Interpreter mode is "interpreting".
     * */

    override fun interpret(token: String) {
        if (D) vm.dbg(3, "vm.interpInterpret: $token")
        val w: Word? = vm.dict.getSafe(token)

        if (w != null) {
            if (w.compO) throw InvalidState("Compile-only: $w")
            vm.currentWord = w
            w.fn(vm)
        } else {
            vm.dstk.push(token.toForthInt(vm.base))
        }
    }


    // ********************************************************** bootstrapping


    /** Evaluate a line during bootstrapping.
     *
     * This is during the bootstrapping; it doesn't use the "real" interpreter;
     * this is needed to compile the real interpreter and put it at the start
     * of cstart.
     *
     * */

    fun bootstrapEval(line: String) {
        // The "mini-interpreter" is located in scratch space here. It is only
        // needed for bootstrapping, so it will get junked when the system
        // uses that space for other scratch stuff.

        vm.ip = vm.memConfig.scratchStart
        vm.source.scanner.fill(line)
        try {
            vm.innerRunVM()
        } catch (_: IntBrk) {
            return
        }
    }

    /** Set up bootstrap evaluator. */

    fun setUpBootstrapEval() {
        // Poke in the mini-interpreter in the scratch space.
        vm.cend = vm.memConfig.scratchStart

        // This is the smallest thing that could be called an interpreter:
        // it doesn't REFILL to get another line (so it can only be an
        // interpreter for one line of code and if that's already placed in
        // the scanner buffer).

        vm.appendWord("PARSE-NAME")
        vm.appendWord("DUP")
        vm.appendJump("0BRANCH", 4) // to 2drop
        vm.appendWord("BOOTSTRAP-PROCESS-TOKEN")
        vm.appendJump("BRANCH", -6) // back to start: another token
        vm.appendWord("2DROP")
        vm.appendWord("BRK")

        vm.cend = vm.cstart
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
                if (D || vm.verbosity >= 3) {
                    vm.io.info("\nInterpreter is:")
                    wToolsCustom.w_dotCode(vm)
                    vm.io.println()
                }
            }
    }
}