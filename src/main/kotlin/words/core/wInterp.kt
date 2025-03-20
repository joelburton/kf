package kf.words.core

import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.danger
import kf.ForthVM
import kf.IWordClass
import kf.TerminalStringInterface
import kf.Word
import kf.strFromAddrLen
import kf.interps.InterpBase

object wInterp : IWordClass {
    override val name = "Interp"
    override val description = "Fundamental words for the VM"

    override val words
        get() = arrayOf(
            Word("ABORT", ::w_abort),
            Word("ABORT\"", ::w_abortQuote),
            Word("EVALUATE", ::w_evaluate),
            Word("QUIT", ::w_quit),
            Word("STATE", ::w_state),
            Word("[", ::w_leftBracket, imm = true, compO = true),
            Word("]", ::w_rightBracket, imm = true),
        )


    // **************************************************** exiting & restarting

    /** ABORT      CORE
     *
     * ( i * x -- ) ( R: j * x -- )
     *
     * Empty the data stack and perform the function of QUIT, which includes
     * emptying the return stack, without displaying a message.
     *
     */

    fun w_abort(vm: ForthVM) {
        vm.dstk.reset()
//        w_quit(vm)  fixme : need to fix quit!
        vm.reset()
    }

    /**
     * ABORT"       CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     *      ( "ccc<quote>" -- )
     * Parse ccc delimited by a " (double-quote). Append the run-time semantics
     * given below to the current definition.
     *
     * Run-time:
     *      ( i * x x1 -- | i * x ) ( R: j * x -- | j * x )
     * Remove x1 from the stack. If any bit of x1 is not zero, display ccc and
     * perform an implementation-defined abort sequence that includes the
     * function of ABORT.
     */

    private fun w_abortQuote(vm: ForthVM) {
        // fixme: needs to be fixed for compilation
        val s = vm.interp.scanner.parse('"').strFromAddrLen(vm)
        val flag: Int = vm.dstk.pop()
        if (flag != 0) {
            vm.io.danger("ABORT: $s")
            w_abort(vm)
//            vm.reset()
        }
    }

    /** QUIT      CORE
     *
     *    ( -- ) ( R: i * x -- )
     *
     * Empty the return stack, store zero in SOURCE-ID if it is present,
     * make the user input device the input source, and enter interpretation
     * state. Do not display a message. Repeat the following:
     *
     * - Accept a line from the input source into the input buffer, set >IN to
     *   zero, and interpret.
     * - Display the implementation-defined system prompt if in interpretation
     *   state, all processing has been completed, and no ambiguous condition
     *   exists.
     */

    fun w_quit(vm: ForthVM) {
        vm.rstk.reset()
        while (true) {
            val input = vm.io.readLineOrNull(false)
            if (input == null) throw RuntimeException("EOF")
            vm.interp.scanner.fill(input)
//            interpretLoop(vm) fixme
            println(yellow("   ok"))
        }
    }


    /** EVALUATE     CORE
     *
     *   ( i * x c-addr u -- j * x )
     *
     * Save the current input source specification. Store minus-one (-1) in
     * SOURCE-ID if it is present. Make the string described by c-addr and u
     * both the input source and input buffer, set >IN to zero, and interpret.
     * When the parse area is empty, restore the prior input source
     * specification. Other stack effects are due to the words EVALUATEd.
     *
     */

    fun w_evaluate(vm: ForthVM) {

        // TODO: this might not be the best approach; we'd want
        // everything the same: ANSI, raw-term-ability, etc
        // better perhaps: being able to "stuff" input into
        // the normal input?
        // or, even better: a different string buffer loc

        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val s = Pair(addr, len).strFromAddrLen(vm)

        val prevIO = vm.io
//        val prevVerbosity = vm.verbosity

        vm.io = Terminal(terminalInterface = TerminalStringInterface(s))
//        vm.verbosity = -2

        try {
//            interpretLoop(vm)   fixme
        } finally {
            vm.io = prevIO
//            vm.verbosity = prevVerbosity
        }

    }

    /**
     * STATE     CORE
     *
     * ( -- a-addr )
     *
     * a-addr is the address of a cell containing the compilation-state flag.
     * STATE is true when in compilation state, false otherwise. The true value
     * in STATE is non-zero, but is otherwise implementation-defined. Only the
     * following standard words alter the value in STATE:
     * : (colon), ; (semicolon), ABORT, QUIT, :NONAME, [ (left-bracket),
     * ] (right-bracket).
     */

    fun w_state(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_STATE)
    }

    /** [    left-bracket    CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * Perform the execution semantics given below.
     *
     * Execution:
     * ( -- )
     * Enter interpretation state. [ is an immediate word.
     */

    fun w_leftBracket(vm: ForthVM) {
        vm.interp.state = InterpBase.STATE_INTERPRETING
    }

    /** ]    right-bracket    CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * Perform the execution semantics given below.
     *
     * Execution:
     * ( -- )
     * Enter interpretation state. ] is an immediate word.
     */

    fun w_rightBracket(vm: ForthVM) {
        vm.interp.state = InterpBase.STATE_COMPILING
    }
}

