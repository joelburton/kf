package kf.primitives

import kf.D
import kf.ForthVM
import kf.Word
import kf.WordClass

object WLoops: WordClass {
    override val name = "Loops"
    override val primitives: Array<Word> = arrayOf(
        Word("begin", ::w_begin , imm = true, compO = true) ,
        Word("again", ::w_again , imm = true, compO = true) ,
        Word("until", ::w_until , imm = true, compO = true) ,
        Word("while", ::w_while , imm = true, compO = true) ,
        Word("repeat", ::w_repeat , imm = true, compO = true) ,

        Word("do", ::w_do , imm = true, compO = true) ,
        Word("(do)", ::w_parenDo , compO = true, hidden = true) ,
        Word("loop", ::w_loop , imm = true, compO = true) ,
        Word("i", ::w_i , compO = true) ,
        Word("j", ::w_j , compO = true) ,
        Word("k", ::w_k , compO = true) ,
        Word("l", ::w_l , compO = true) ,
        Word("m", ::w_m , compO = true) ,

        Word("(loop)", ::w_parenLoop , compO = true, hidden = true) ,
        Word("+loop", ::w_plusLoop , imm = true, compO = true) ,

        Word("leave", ::w_leave , imm = true, compO = true) ,

        Word(".lstk", ::w_lstk ) ,  // ?do - enter loop if true
        // -do down-counting

    )


    /**  begin (a loop) */
    fun w_begin(vm: ForthVM) {
        vm.dstk.push(vm.cend)
    }

    /**  again */
    fun w_again(vm: ForthVM) {
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    /**  until */
    fun w_until(vm: ForthVM) {
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    private fun w_while(vm: ForthVM) {
        vm.appendJump("0branch", 0xffff)
        vm.dstk.push(vm.cend) // location of while
    }

    private fun w_repeat(vm: ForthVM) {
        val whileRef: Int = vm.dstk.pop()
        vm.mem[whileRef - 1] = vm.cend + 2 // failing while goes past me
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
        vm.appendWord("drop")
    }


    /**  loop  (limit start -- limit start R:addr  ) */
    fun w_do(vm: ForthVM) {
        vm.appendWord("(do)")
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us

        // a jump just to jump over the next jump
        vm.appendJump("branch", vm.cend + 4)
        // a jump to the end of the loop (will be filled in by `loop`)
        vm.appendJump("branch", 0xffff)
    }

    // "do"  adds this to def (this is actually run at runtime)
    fun w_parenDo(vm: ForthVM) {
        // swap
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(a, b)

        // >L
        vm.lstk.push(vm.dstk.pop())

        // >L
        vm.lstk.push(vm.dstk.pop())
    }

    /**  loop */
    fun w_loop(vm: ForthVM) {
        vm.appendLit(1)
        val bwref: Int = vm.rstk.pop() // beginning of loop
        vm.appendJump("(loop)", bwref + 4)

        // fix jump-at-start (For leave)
        vm.mem[bwref + 3] = vm.cend
    }

    /**  +loop */
    fun w_plusLoop(vm: ForthVM) {
        val bwref: Int = vm.rstk.pop() // beginning of loop
        vm.appendJump("(loop)", bwref + 4)

        // fix jump-at-start (For leave)
        vm.mem[bwref + 3] = vm.cend
    }

    // loop-fn
    fun w_parenLoop(vm: ForthVM) {
        val incrementBy = vm.dstk.pop()
        vm.lstk.push(vm.lstk.pop() + incrementBy)
        val loopIdx = vm.lstk.pop()
        val limit: Int = vm.lstk.pop()
        if (loopIdx >= limit) {
            if (D) vm.dbg(3, "w_loopImpl: done")
            vm.ip += 1
        } else {
            if (D) vm.dbg(3, "w_loopImpl: looping")
            // go to the beginning of the loop
            vm.lstk.push(limit, loopIdx)
            vm.ip = vm.mem[vm.ip]
        }
    }

    private fun w_i(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getAt(1))
    }

    private fun w_j(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getAt(3))
    }

    private fun w_k(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getAt(5))
    }

    private fun w_l(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getAt(7))
    }

    private fun w_m(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getAt(9))
    }

    private fun w_leave(vm: ForthVM) {
        val leaveAddr: Int = vm.rstk.peek() + 2
        vm.appendWord("L>")
        vm.appendWord("L>")
        vm.appendWord("drop")
        vm.appendWord("drop")
        vm.appendJump("jump", leaveAddr)
    }

    private fun w_lstk(vm: ForthVM) {
        vm.lstk.dump()
    }
} // : *begin .cend @ . .cend @ immediate ;
// : *again 'z' emit ['] branch ,, ,, immediate ;  : test 10 *begin 'A' emit 1- dup 0= if return then *again ; test