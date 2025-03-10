package kf

class WLoops(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        Word("begin", immediate = true, compileOnly = true) { _ -> w_begin() },
        Word("again", immediate=true, compileOnly=true) { _ -> w_again() },
        Word("until", immediate=true, compileOnly=true) { _ -> w_until() },
        Word("while", immediate=true, compileOnly=true) { _ -> w_while() },
        Word("repeat", immediate=true, compileOnly=true) { _ -> w_repeat() },

        Word("do", immediate=true, compileOnly=true) { _ -> w_do() },
        Word("do-impl", compileOnly = true, hidden = true) { _ -> w_doImpl() },
        Word("loop", immediate=true, compileOnly=true) { _ -> w_loop() },
        Word("i", compileOnly=true) { _ -> w_i() },
        Word("j", compileOnly=true) { _ -> w_j() },
        Word("k", compileOnly=true) { _ -> w_k() },
        Word("l", compileOnly=true) { _ -> w_l() },
        Word("m", compileOnly=true) { _ -> w_m() },

        Word("loop-impl", compileOnly = true, hidden = true) { _ -> w_loopImpl() },
        Word("+loop", immediate=true, compileOnly=true) { _ -> w_plusLoop() },

        Word("leave", immediate=true, compileOnly=true) { _ -> w_leave() },

        Word(".lstk") { _ -> w_lstk() },  // ?do - enter loop if true
        // -do down-counting

    )


    /**  begin (a loop) */
    fun w_begin() {
        if (D) vm.dbg("w_begin")
        vm.dstk.push(vm.cend)
    }

    /**  again */
    fun w_again() {
        if (D) vm.dbg("w_again")
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    /**  until */
    fun w_until() {
        if (D) vm.dbg("w_until")
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    private fun w_while() {
        if (D) vm.dbg("while")
        vm.appendJump("0branch", 0xffff)
        vm.dstk.push(vm.cend) // location of while
    }

    private fun w_repeat() {
        if (D) vm.dbg("repeat")
        val while_ref: Int = vm.dstk.pop()
        vm.mem[while_ref - 1] = vm.cend + 2 // failing while goes past me
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
        vm.appendWord("drop")
    }


    /**  loop  (limit start -- limit start R:addr  ) */
    fun w_do() {
        if (D) vm.dbg("w_do")
        vm.appendWord("do-impl")
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us

        // a jump just to jump over the next jump
        vm.appendJump("branch", vm.cend + 3)
        // a jump to the end of the loop (will be filled in by `loop`)
        vm.appendJump("branch", 0xffff)
    }

    // "do"  adds this to def (this is actually run at runtime)
    fun w_doImpl() {
//        StackOps.w_swap()   // FIXME: gotta be a fast way
//        StackOps.w_toL()
//        StackOps.w_toL()
    }

    /**  loop */
    fun w_loop() {
        if (D) vm.dbg("w_loop")
        vm.appendLit(1)
        val bwref: Int = vm.rstk.pop() // beginning of loop
        vm.appendJump("loop-impl", bwref + 4)

        // fix jump-at-start (For leave)
        vm.mem[bwref + 3] = vm.cend
    }

    /**  +loop */
    fun w_plusLoop() {
        if (D) vm.dbg("w_loopPlus")
        val bwref: Int = vm.rstk.pop() // beginning of loop
        vm.appendJump("loop-impl", bwref + 4)

        // fix jump-at-start (For leave)
        vm.mem[bwref + 3] = vm.cend
    }

    // loop-fn
    fun w_loopImpl() {
        if (D) vm.dbg("w_loopPlusFn")
        val incrementBy: Int = vm.dstk.pop()
        vm.lstk.push(vm.lstk.pop() + incrementBy)
        val loopIdx: Int = vm.lstk.pop()
        val limit: Int = vm.lstk.pop()
        if (loopIdx >= limit) {
            if (D) vm.dbg("w_loopFnPlus: done")
            vm.cptr += 1
        } else {
            if (D) vm.dbg("w_loopFnPlus: looping")
            // go to the beginning of the loop
            vm.lstk.push(limit, loopIdx)
            vm.cptr = vm.mem.get(vm.cptr)
        }
    }

    private fun w_i() {
        if (D) vm.dbg("i")
        vm.dstk.push(vm.lstk.getAt(1))
    }

    private fun w_j() {
        if (D) vm.dbg("j")
        vm.dstk.push(vm.lstk.getAt(3))
    }

    private fun w_k() {
        if (D) vm.dbg("k")
        vm.dstk.push(vm.lstk.getAt(5))
    }

    private fun w_l() {
        if (D) vm.dbg("l")
        vm.dstk.push(vm.lstk.getAt(7))
    }

    private fun w_m() {
        if (D) vm.dbg("m")
        vm.dstk.push(vm.lstk.getAt(9))
    }

    private fun w_leave() {
        if (D) vm.dbg("leave")
        val leaveAddr: Int = vm.rstk.peek() + 2
        vm.appendWord("L>")
        vm.appendWord("L>")
        vm.appendWord("drop")
        vm.appendWord("drop")
        vm.appendJump("jump", leaveAddr)
    }

    private fun w_lstk() {
        vm.lstk.dump()
    }
} // : *begin .cend @ . .cend @ immediate ;
// : *again 'z' emit ['] branch ,, ,, immediate ;  : test 10 *begin 'A' emit 1- dup 0= if return then *again ; test