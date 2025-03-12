package kf

class WLoops(val vm: ForthVM): WordClass {
    override val name = "Loops"
    override val primitives: Array<Word> = arrayOf(
        Word("begin", imm = true, compO = true) { w_begin() },
        Word("again", imm=true, compO=true) { w_again() },
        Word("until", imm=true, compO=true) { w_until() },
        Word("while", imm=true, compO=true) { w_while() },
        Word("repeat", imm=true, compO=true) { w_repeat() },

        Word("do", imm=true, compO=true) { w_do() },
        Word("(do)", compO = true, hidden = true) { w_parenDo() },
        Word("loop", imm=true, compO=true) { w_loop() },
        Word("i", compO=true) { w_i() },
        Word("j", compO=true) { w_j() },
        Word("k", compO=true) { w_k() },
        Word("l", compO=true) { w_l() },
        Word("m", compO=true) { w_m() },

        Word("(loop)", compO = true, hidden = true) { w_parenLoop() },
        Word("+loop", imm=true, compO=true) { w_plusLoop() },

        Word("leave", imm=true, compO=true) { w_leave() },

        Word(".lstk") { w_lstk() },  // ?do - enter loop if true
        // -do down-counting

    )


    /**  begin (a loop) */
    fun w_begin() {
        vm.dstk.push(vm.cend)
    }

    /**  again */
    fun w_again() {
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    /**  until */
    fun w_until() {
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    private fun w_while() {
        vm.appendJump("0branch", 0xffff)
        vm.dstk.push(vm.cend) // location of while
    }

    private fun w_repeat() {
        val while_ref: Int = vm.dstk.pop()
        vm.mem[while_ref - 1] = vm.cend + 2 // failing while goes past me
        val bwref: Int = vm.dstk.pop()
        vm.appendJump("branch", bwref)
        vm.appendWord("drop")
    }


    /**  loop  (limit start -- limit start R:addr  ) */
    fun w_do() {
        vm.appendWord("do-impl")
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us

        // a jump just to jump over the next jump
        vm.appendJump("branch", vm.cend + 4)
        // a jump to the end of the loop (will be filled in by `loop`)
        vm.appendJump("branch", 0xffff)
    }

    // "do"  adds this to def (this is actually run at runtime)
    fun w_parenDo() {
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
    fun w_loop() {
        vm.appendLit(1)
        val bwref: Int = vm.rstk.pop() // beginning of loop
        vm.appendJump("loop-impl", bwref + 4)

        // fix jump-at-start (For leave)
        vm.mem[bwref + 3] = vm.cend
    }

    /**  +loop */
    fun w_plusLoop() {
        val bwref: Int = vm.rstk.pop() // beginning of loop
        vm.appendJump("loop-impl", bwref + 4)

        // fix jump-at-start (For leave)
        vm.mem[bwref + 3] = vm.cend
    }

    // loop-fn
    fun w_parenLoop() {
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

    private fun w_i() {
        vm.dstk.push(vm.lstk.getAt(1))
    }

    private fun w_j() {
        vm.dstk.push(vm.lstk.getAt(3))
    }

    private fun w_k() {
        vm.dstk.push(vm.lstk.getAt(5))
    }

    private fun w_l() {
        vm.dstk.push(vm.lstk.getAt(7))
    }

    private fun w_m() {
        vm.dstk.push(vm.lstk.getAt(9))
    }

    private fun w_leave() {
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