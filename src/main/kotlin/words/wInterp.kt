package kf.words

import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.danger
import kf.*
import kf.ForthVM.Companion.INTERP_STATE_INTERPRETING
import kf.primitives.WInterp.w_banner

object wInterp : IWordClass {
    override val name = "Interpreter"
    override val description = "Fundamental words for the VM"

    override val words
        get() = arrayOf(
            Word("ABORT", ::w_abort),
            Word("ABORT\"", ::w_abortQuote),
            Word("EVALUATE", ::w_evaluate),
//        Word("bye", ::w_bye),
//        Word("cold", ::w_cold, imm = true, interpO = true),
            Word("QUIT", ::w_quit),
            Word("STATE", ::w_state),
            Word("[", ::w_leftBracket, imm = true, compO = true),
            Word("]", ::w_rightBracket, imm = true),

//            Word("process-token2", ::w_processToken2, interpO = true),
            Word("my-interpret", ::w_myInterpret),
            Word("my-compile", ::w_myCompile, compO = true),
        )


    // ******************************************************* Interpreter modes

    /** Called by w_processToken when InterpretedMode is "compiling":
     *
     * Most parts in the definition and just added directly.
     * However, words that are "immediate-mode" will execute.
     */

    fun _compile(vm: ForthVM, token: String) {
        if (D) vm.dbg(3, "vm.interpCompile: $token")
        val w: Word? = vm.dict.getSafeChkRecursion(token, vm.io)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: ${w.name}")
            else if (w.imm) {
                w(vm)
            } else {
                vm.appendCode(w.wn, CellMeta.WordNum)
            }
        } else if (token.isCharLit) {
            vm.appendLit(token[1].code)
        } else {
            val n: Int = token.toForthInt(vm.base)
            vm.appendWord("lit")
            vm.appendCode(n, CellMeta.NumLit)
        }
    }
    fun compile(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val token = vm.interpScanner.getAsString(addr, len)
        _compile(vm, token)
    }

    /**  Called by w_processToken when Interpreter mode is "interpreting":
     *
     * Execute current token: if a word, run it; else, try as number.
     */

    fun interpret(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val token = vm.interpScanner.getAsString(addr, len)

        if (D) vm.dbg(3, "vm.interpInterpret: $token")
        val w: Word? = vm.dict.getSafe(token)
        if (w != null) {
            if (w.compO) throw InvalidState("Compile-only: " + w.name)
            w(vm)
        } else if (token.isCharLit) {
            vm.dstk.push(token[1].code)
        } else {
            vm.dstk.push(token.toForthInt(vm.base))
        }
    }

//    fun interpretLoop(vm: ForthVM) {
//        while (true) {
//            if (vm.getToken().length == 0) break
//            val token = vm.interpToken
//            if (vm.isCompilingState) compile(vm, token)
//            else interpret(vm, token)
//        }
//    }

//    fun w_processToken2(vm: ForthVM) {
//        if (vm.isInterpretingState) {
//            interpret(vm, token)
//        } else {
//            compile(vm, token)
//        }
//    }

    fun w_myInterpret(vm: ForthVM) {
        interpret(vm)
    }
    fun w_myCompile(vm: ForthVM) {
        compile(vm)
    }

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
    fun addInterpreterCode(vm: ForthVM) {
        if (D) vm.dbg(3, "vm.addInterpreterCode")

        // fixme: can't use lots of things here because this method is too
        //   fake --- the split-on-whitespace doesn't act like the real
        //   interpreter for parsing

        """
        begin 
            refill while
                begin
                    word dup @ while
                        state @ if
                            find ?dup if
                                1 = if
                                    execute
                                else
                                    ,,
                                then
                            else
                                count 0 0 2swap >number drop drop drop
                                do-lit ,, ,,
                            then
                        else
                            find if
                                execute
                            else
                                count 0 0 2swap >number drop drop drop
                            then
                        then
                    repeat
                drop
                3 spaces        
                111 emit 107 emit
                cr
            repeat
        eof
        """
            .trimIndent()
            .split(Regex("\\s+"))
            .forEach { _compile(vm, it) }
            .also { wToolsExtra.w_dumpCode(vm) }
//
//        vm.appendWord("interp-prompt")
//        vm.appendWord("refill")
//        vm.appendJump("0branch", 7) // eof, -> eof
//        vm.appendWord("interp-read")
//        vm.appendJump("0branch", -6) // no more token, -> refill
//
////        vm.appendWord("interp-process")
//        vm.appendWord("state")
//        vm.appendJump("0branch", +5)
//
//
//        vm.appendJump("branch", -5) // go back to read
//        vm.appendWord("eof")
//        vm.appendJump("branch", -12) // jump back to start
    }

    // ************************************************ Reboot/reset interpreter

    /**  Handle a VM reboot at the interpreter layer.
     */
    fun rebootInterpreter(vm: ForthVM) {
        if (D) vm.dbg(3, "vm.rebootInterpreter")
        vm.interpToken = ""
        vm.interpScanner.reset()

        // Put interpreter code in mem; the VM will start executing here
        addInterpreterCode(vm)
        resetInterpreter(vm)
        w_banner(vm)
    }

    /**  Handle a VM reset at the interpreter layer.
     */
    fun resetInterpreter(vm: ForthVM) {
        if (D) vm.dbg(3, "vm.resetInterpreter")

        // If error happens while defining word, roll back this word.
        vm.dict.currentlyDefining?.let { w ->
            vm.cend = w.cpos
            vm.dict.removeLast()
            vm.dict.currentlyDefining = null
        }
        vm.interpState = INTERP_STATE_INTERPRETING
    }


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
        val (addr, len) = vm.interpScanner.parse('"')
        val s = vm.interpScanner.getAsString(addr, len)
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
            vm.interpScanner.fill(input)
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
        val s = vm.interpScanner.getAsString(addr, len)

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
        vm.dstk.push(ForthVM.REG_INTERP_STATE)
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
        vm.interpState = ForthVM.INTERP_STATE_INTERPRETING
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
        vm.interpState = ForthVM.INTERP_STATE_COMPILING
    }
}

