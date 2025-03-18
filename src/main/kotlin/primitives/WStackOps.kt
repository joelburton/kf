package kf.primitives

import kf.ForthError
import kf.ForthVM
import kf.Word
import kf.WordClass
import kf.words.wLowLevel.w_nop

object WStackOps: WordClass {
    override val name = "Stack Operations"
    override val primitives get() = arrayOf(
        Word("nip", ::w_nip ) ,
        Word("pick", ::w_pick ) ,
        Word("tuck", ::w_tuck ) ,
        Word("roll", ::w_roll ) ,

        Word("clearstack", ::w_clearStack ) ,

        Word("sp0", ::w_sp0 ) ,
        Word("sp@", ::w_spFetch ) ,
        Word("sp!", ::w_spStore ) ,

        Word("rp0", ::w_rp0 ) ,
        Word("rp@", ::w_rpFetch ) ,
        Word("rp!", ::w_rpStore ) ,
        Word(">R", ::w_toR ) ,
        Word("R>", ::w_rFrom ) ,  // R@ - copy to dstk

        Word("?stack", ::w_nop)
        // should these all be C for control stack?
        // clearstacks : clears data & fp, not other things!

    )


    // roll -- 2 roll is rot, 1 roll is swap, etc   3 a b c d e => c d e a b
    /**  `roll` ( for:n=3 a b c d e n -- a c d e b : move nth-from-top to top )
     *
     * - 2 roll is same as rot:  a b c 2 -- b c a
     * - 1 roll is swap:         a b 1   -- b a
     * - 0 roll is no-op:        a b 0   -- a b
     */
    private fun w_roll(vm: ForthVM) {
        vm.dstk.push(vm.dstk.popFrom(vm.dstk.pop()))
    }

    /**  `pick` ( for:n=3 a b c d n -- a b c d b : copy nth-from-top to top )
     */
    private fun w_pick(vm: ForthVM) {
        vm.dstk.push(vm.dstk.getFrom(vm.dstk.pop()))
    }

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_sp0(vm: ForthVM) {
        vm.dstk.push(vm.dstk.startAt - 1)
    }

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_rp0(vm: ForthVM) {
        vm.dstk.push(vm.rstk.startAt - 1)
    }

    /**  `clearstack` ( ? ? -- : clear entire data stack )
     */
    private fun w_clearStack(vm: ForthVM) {
        vm.dstk.reset()
    }


    /**  `>R` ( n -- r:n : move top of dstk to rstk )
     */
    fun w_toR(vm: ForthVM) {
        vm.rstk.push(vm.dstk.pop())
    }

    /**  `R>`( r:n -- n : move top of rstk to dstk )
     */
    fun w_rFrom(vm: ForthVM) {
        vm.dstk.push(vm.rstk.pop())
    }

    /** `nip` ( n1 n2 -- n2 : drop second item from stack )
     */
    fun w_nip(vm: ForthVM) {
        val a: Int = vm.dstk.pop()
        vm.dstk.pop()
        vm.dstk.push(a)
    }


    /**  `sp@` ( -- addr : pushes dstk sp to stack )
     */
    fun w_spFetch(vm: ForthVM) {
        vm.dstk.push(vm.dstk.sp)
    }

    /**  `sp!` ( addr -- : sets dstk sp to addr )
     */
    fun w_spStore(vm: ForthVM) {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.dstk.endAt) {
            throw ForthError("Invalid sp")
        }
        vm.dstk.sp = sp
    }

    /**  `rp@` ( -- addr : pushes rstk sp to stack )
     */
    fun w_rpFetch(vm: ForthVM) {
        vm.dstk.push(vm.rstk.sp)
    }

    /**  `rp!` ( addr -- : sets rstk sp to addr )
     */
    fun w_rpStore(vm: ForthVM) {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.rstk.endAt) {
            throw ForthError("Invalid sp")
        }
        vm.rstk.sp = sp
    }


    /**  `tuck` ( a b -- b a b : copy top item to behind second )
     */
    private fun w_tuck(vm: ForthVM) {
        val n2: Int = vm.dstk.pop()
        val n1: Int = vm.dstk.pop()
        vm.dstk.push(n2, n1, n2)
    }
}