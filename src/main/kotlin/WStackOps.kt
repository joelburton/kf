package kf

class
WStackOps(val vm: ForthVM): WordClass {
    override val name = "Stack Operations"
    override val primitives: Array<Word> = arrayOf<Word>(
        Word("dup") { _ -> w_dup() },
        Word("drop") { _ -> w_drop() },
        Word("swap") { _ -> w_swap() },
        Word("nip") { _ -> w_nip() },
        Word("over") { _ -> w_over() },
        Word("rot") { _ -> w_rot() },
        Word("pick") { _ -> w_pick() },
        Word("tuck") { _ -> w_tuck() },
        Word("roll") { _ -> w_roll() },
        Word("?dup") { _ -> w_questionDup() },

        Word("2drop") { _ -> w_2drop() },
        Word("2dup") { _ -> w_2dup() },
        Word("2over") { _ -> w_2over() },
        Word("2swap") { _ -> w_2swap() },

        Word(".s") { _ -> w_dotS() },
        Word("clearstack") { _ -> w_clearStack() },
        Word("depth") { _ -> w_depth() },

        Word("sp0") { _ -> w_sp0() },
        Word("sp@") { _ -> w_spFetch() },
        Word("sp!") { _ -> w_spStore() },

        Word("rp0") { _ -> w_rp0() },
        Word("rp@") { _ -> w_rpFetch() },
        Word("rp!") { _ -> w_rpStore() },
        Word(">R") { _ -> w_toR() },
        Word("R>") { _ -> w_rFrom() },  // R@ - copy to dstk
        
        Word("lp0") { _ -> w_lp0() },
        Word("lp@") { _ -> w_lpFetch() },
        Word("lp!") { _ -> w_lpStore() },
        Word(">L") { _ -> w_toL() },
        Word("L>") { _ -> w_lFrom() },  // L@
        // should these all be C for control stack?
        // clearstacks : clears data & fp, not other things!

    )

    /**  `2drop` ( n1 n2 -- : drop top two items )
     */
    private fun w_2drop() {
        vm.dstk.pop()
        vm.dstk.pop()
    }

    /**  `2dup` ( n1 n2 -- n1 n2 n1 n2 : duplicates top two items )
     */
    private fun w_2dup() {
        val b: Int = vm.dstk.pop()
        val a: Int = vm.dstk.pop()
        vm.dstk.push(a, b, a, b)
    }

    /**  `2over` ( a b c d -- a b c d a b : copy a and b to top )
     */
    private fun w_2over() {
        vm.dstk.push(vm.dstk.getFrom(3), vm.dstk.getFrom(3))
    }

    /** `2swap` ( a b c d -- c d a b : swap two pairs )
     */
    private fun w_2swap() {
        val d: Int = vm.dstk.pop()
        val c: Int = vm.dstk.pop()
        val b: Int = vm.dstk.pop()
        val a: Int = vm.dstk.pop()
        vm.dstk.push(c, d, a, b)
    }

    /**  `2dup` ( a -- a a-if-not-0 : duplicates top item if nonzero )
     */
    private fun w_questionDup() {
        val v: Int = vm.dstk.peek()
        if (v != 0) vm.dstk.push(v)
    }

    // roll -- 2 roll is rot, 1 roll is swap, etc   3 a b c d e => c d e a b
    /**  `roll` ( for:n=3 a b c d e n -- a c d e b : move nth-from-top to top )
     *
     * - 2 roll is same as rot:  a b c 2 -- b c a
     * - 1 roll is swap:         a b 1   -- b a
     * - 0 roll is no-op:        a b 0   -- a b
     */
    private fun w_roll() {
        vm.dstk.push(vm.dstk.popFrom(vm.dstk.pop()))
    }

    /**  `pick` ( for:n=3 a b c d n -- a b c d b : copy nth-from-top to top )
     */
    private fun w_pick() {
        vm.dstk.push(vm.dstk.getFrom(vm.dstk.pop()))
    }

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_sp0() {
        vm.dstk.push(vm.dstk.startAt - 1)
    }

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_rp0() {
        vm.dstk.push(vm.rstk.startAt - 1)
    }

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_lp0() {
        vm.dstk.push(vm.lstk.startAt - 1)
    }

    /**  `rot` ( a b c -- b c a : rotates top 3 items left )
     */
    fun w_rot() {
        vm.dstk.push(vm.dstk.getFrom(2))
    }

    /**  `clearstack` ( ? ? -- : clear entire data stack )
     */
    private fun w_clearStack() {
        vm.dstk.reset()
    }

    /**  `dup` ( n -- n n : duplicate top of stack )
     */
    fun w_dup() {
        val v = vm.dstk.peek()
        vm.dstk.push(v)
    }

    /**  `drop` ( n -- drop top of stack )
     */
    fun w_drop() {
        vm.dstk.pop()
    }

    /**  ( n1 n2 -- n2 n1 : swap top two items )
     */
    fun w_swap() {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(a, b)
    }

    /**  `>R` ( n -- r:n : move top of dstk to rstk )
     */
    fun w_toR() {
        vm.rstk.push(vm.dstk.pop())
    }

    /**  `R>`( r:n -- n : move top of rstk to dstk )
     */
    fun w_rFrom() {
        vm.dstk.push(vm.rstk.pop())
    }

    /**  `>L` ( n -- l:n : move top of dstk to lstk )
     */
    fun w_toL() {
        vm.lstk.push(vm.dstk.pop())
    }

    /**  `>L` ( l:n -- n : move top of lstk to dstk )
     */
    fun w_lFrom() {
        vm.dstk.push(vm.lstk.pop())
    }

    /** `nip` ( n1 n2 -- n2 : drop second item from stack )
     */
    fun w_nip() {
        val a: Int = vm.dstk.pop()
        vm.dstk.pop()
        vm.dstk.push(a)
    }

    /**  `over` ( n1 n2 -- n1 n2 n1 : copy second item to top )
     */
    fun w_over() {
        val a: Int = vm.dstk.pop()
        val b: Int = vm.dstk.peek()
        vm.dstk.push(a, b)
    }

    /**  `sp@` ( -- addr : pushes dstk sp to stack )
     */
    fun w_spFetch() {
        vm.dstk.push(vm.dstk.sp)
    }

    /**  `sp!` ( addr -- : sets dstk sp to addr )
     */
    fun w_spStore() {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.dstk.size) {
            throw ForthError("Invalid sp")
        }
        vm.dstk.sp = sp
    }

    /**  `rp@` ( -- addr : pushes rstk sp to stack )
     */
    fun w_rpFetch() {
        vm.dstk.push(vm.rstk.sp)
    }

    /**  `rp!` ( addr -- : sets rstk sp to addr )
     */
    fun w_rpStore() {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.rstk.size) {
            throw ForthError("Invalid sp")
        }
        vm.rstk.sp = sp
    }

    /**  `lp@` ( -- addr : pushes lstk sp to stack )
     */
    fun w_lpFetch() {
        vm.dstk.push(vm.lstk.sp)
    }

    /**  `lp!` ( addr -- : sets lstk sp to addr )
     */
    fun w_lpStore() {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.lstk.size) {
            throw ForthError("Invalid sp")
        }
        vm.lstk.sp = sp
    }

    /**  `depth` ( a b c -- a b c 3 : returns depth of items on stack )
     */
    fun w_depth() {
        vm.dstk.push(vm.dstk.size)
    }

    /**  `.s` ( -- : short listing of stack items )
     */
    fun w_dotS() {
        vm.dstk.simpleDump()
    }

    /**  `tuck` ( a b -- b a b : copy top item to behind second )
     */
    private fun w_tuck() {
        val n2: Int = vm.dstk.pop()
        val n1: Int = vm.dstk.pop()
        vm.dstk.push(n2, n1, n2)
    }
}