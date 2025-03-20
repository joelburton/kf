package kf.words.core

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl


object wRStack: IWordClass {
    override val name = "RStack"
    override val description = "Handling calling and exiting custom functions"

    override val words = arrayOf(
        Word(">R", ::w_toR),
        Word("R>", ::w_rFrom),
        Word("R@", ::w_rFetch),
    )



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

    /**
     * ( -- x ) ( R: x -- x )
     * Copy x from the return stack to the data stack.
     */
    fun w_rFetch(vm: ForthVM) {
        vm.dstk.push(vm.rstk.peek())
    }


}


