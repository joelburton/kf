package kf.words.core

import kf.*
import kf.dict.IWordModule
import kf.dict.Word
import kf.interps.InterpBase
import kf.interps.InterpEval
import kf.sources.SourceEval

object wInterp : IWordModule {
    override val name = "kf.words.core.wInterp"
    override val description = "Interpreter"

    override val words
        get() = arrayOf(
            Word("EVALUATE", ::w_evaluate),

            Word("QUIT", ::w_quit),
            Word("ABORT", ::w_abort),
            Word("ABORT\"", ::w_abortQuote, imm = true),
            Word("(ABORT\")", ::w_parenAbortQuote),

            Word("STATE", ::w_state),
            Word("[", ::w_leftBracket, imm = true, compO = true),
            Word("]", ::w_rightBracket, imm = true),
        )


    // **************************************************** exiting & restarting

    /** QUIT ( -- ) ( R: i * x -- ) Empty rstk and restart interp loop. */

    fun w_quit(vm: ForthVM) {
        vm.quit()
    }

    /** ABORT ( i * x -- ) ( R: j * x -- ) Do QUIT & empty data stack
     *
     * Empty the data stack and perform the function of QUIT, which includes
     * emptying the return stack, without displaying a message.
     *
     */

    fun w_abort(vm: ForthVM) {
        vm.io.danger("${vm.source} ABORT")
        vm.abort()
    }

    /**
     * ABORT"
     *      ( i * x x1 -- | i * x ) ( R: j * x -- | j * x )
     * Abort conditionally based on flag x1
     */

    fun w_abortQuote(vm: ForthVM) {
        if (vm.interp.isInterpreting) {
            val s = vm.source.scanner.parse('"').strFromAddrLen(vm)
            val flag = vm.dstk.pop()
            if (flag != 0) {
                vm.io.danger("${vm.source} ABORT: $s")
                vm.abort()
            }
        } else {
            val s = vm.source.scanner.parse('"').strFromAddrLen(vm)
            vm.appendJump("0BRANCH", s.length + 4) // jaddr,lit-s,len,abort
            vm.appendStr(s)
            vm.appendWord("(ABORT\")")
        }
    }

    /** `(ABORT")` ( c-addr len -- ) Abort with message. */

    fun w_parenAbortQuote(vm: ForthVM) {
        val s = Pair(vm.dstk.pop(), vm.dstk.pop()).strFromLenAddr(vm)
        vm.io.danger("${vm.source} ABORT: $s")
        vm.dstk.reset()
        w_quit(vm)
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
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()

        if (vm.interp !is InterpEval) throw RuntimeException("not an eval!")
        val s = Pair(addr, len).strFromAddrLen(vm)
        vm.source.push(SourceEval(vm, s))
    }

    /**
     * `STATE` ( -- a-addr ) Push address of cell containing interp state flag.
     *
     * false = not compiling
     * any other vale = compiling
     * */

    fun w_state(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_STATE)
    }

    /** `[` IM CO ( -- ) Enter interp state */

    fun w_leftBracket(vm: ForthVM) {
        vm.interp.state = InterpBase.STATE_INTERPRETING
    }

    /** `]` IM ( -- ) Enter compiling state */

    fun w_rightBracket(vm: ForthVM) {
        vm.interp.state = InterpBase.STATE_COMPILING
    }
}

