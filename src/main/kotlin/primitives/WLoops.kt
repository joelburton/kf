package kf.primitives

import kf.D
import kf.ForthVM
import kf.Word
import kf.WordClass

object WLoops : WordClass {
    override val name = "Loops"
    override val primitives
        get() = arrayOf(
            Word("begin", ::w_begin, imm = true, compO = true),
            Word("again", ::w_again, imm = true, compO = true),

            Word("until", ::w_until, imm = true, compO = true),
            Word("while", ::w_while, imm = true, compO = true),
            Word("repeat", ::w_repeat, imm = true, compO = true),

            Word("do", ::w_do, imm = true, compO = true),
            Word("(do)", ::w_parenDo, compO = true, hidden = true),
            Word("loop", ::w_loop, imm = true, compO = true),
            Word("i", ::w_i, compO = true),
            Word("j", ::w_j, compO = true),
            Word("k", ::w_k, compO = true),

            Word("(loop)", ::w_parenLoop, compO = true, hidden = true),
            Word("(+loop)", ::w_parenPlusLoop, compO = true, hidden = true),
            Word("+loop", ::w_plusLoop, imm = true, compO = true),

            Word("leave", ::w_leave, imm = true, compO = true),

            Word(".lstk", ::w_lstk),  // ?do - enter loop if true
            // -do down-counting

        )


    /**  begin (a loop) */
    fun w_begin(vm: ForthVM) {
        vm.dstk.push(vm.cend)
    }

    /**  again */
    fun w_again(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    /**  until */
    fun w_until(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("0branch", bwref)
    }


    private fun w_while(vm: ForthVM) {
        vm.appendJump("0branch", 0xffff)
        vm.dstk.push(vm.cend) // location of while
    }

    private fun w_repeat(vm: ForthVM) {
        val whileRef = vm.dstk.pop()
        vm.mem[whileRef - 1] = vm.cend + 2 // failing while goes past me
        val bwref = vm.dstk.pop()
        vm.appendJump("branch", bwref)
//        vm.appendWord("drop")       we don't clear this
    }


    /**  loop  (limit start -- limit start R:addr  ) */
    fun w_do(vm: ForthVM) {
        vm.appendWord("(do)")
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us
        vm.rstk.push(0)  // how many forwardrefs to fix
    }

    // "do"  adds this to def (this is actually run at runtime)
    fun w_parenDo(vm: ForthVM) {
        vm.lstk.push(vm.dstk.pop())
        vm.lstk.push(vm.dstk.pop())  // lstk now: ( L: start limit )
    }

    /**  loop */
    fun w_loop(vm: ForthVM) {
        val numExits = vm.rstk.pop()
        vm.appendJump("(loop)", vm.rstk.popFrom(numExits))
        repeat(numExits) { vm.mem[vm.rstk.pop()] = vm.cend }
    }

    /**  +loop */
    fun w_plusLoop(vm: ForthVM) {
        val numExits = vm.rstk.pop()
        vm.appendJump("(+loop)", vm.rstk.popFrom(numExits))
        repeat(numExits) { vm.mem[vm.rstk.pop()] = vm.cend }
    }

    // loop-fn
    fun w_parenLoop(vm: ForthVM) {  // dstk is index limit incby
        runtimeLoop(vm, 1)
    }

    fun w_parenPlusLoop(vm: ForthVM) {  // dstk is index limit incby
        runtimeLoop(vm, vm.dstk.pop())
    }

    fun runtimeLoop(vm: ForthVM, incrementBy: Int) {
        val limit = vm.lstk.pop()
        val index = vm.lstk.pop() + incrementBy
        if (index >= limit) {
            if (D) vm.dbg(3, "w_loopImpl: done")
            vm.ip += 1  // skip the jump-addr after (loop)
        } else {
            if (D) vm.dbg(3, "w_loopImpl: looping")
            // go to the beginning of the loop
            vm.lstk.push(index, limit)
            vm.ip = vm.mem[vm.ip]
        }
    }

    private fun w_i(vm: ForthVM) {
//        vm.dstk.push(vm.lstk.getAt(1))
        vm.dstk.push(vm.lstk.getFrom(1))
    }

    private fun w_j(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getFrom(3))
    }

    private fun w_k(vm: ForthVM) {
        vm.dstk.push(vm.lstk.getFrom(5))
    }

    private fun w_leave(vm: ForthVM) {
        vm.appendWord("L>")
        vm.appendWord("L>")
        vm.appendWord("2drop")
        vm.appendJump("branch", 0xff)
        val count = vm.rstk.pop()
        vm.rstk.push(vm.cend - 1, count + 1)
    }

    private fun w_lstk(vm: ForthVM) {
        vm.lstk.dump()
    }
} // : *begin .cend @ . .cend @ immediate ;
// : *again 'z' emit ['] branch ,, ,, immediate ;  : test 10 *begin 'A' emit 1- dup 0= if return then *again ; test