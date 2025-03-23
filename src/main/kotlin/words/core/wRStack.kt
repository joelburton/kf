package kf.words.core

import kf.ForthVM
import kf.IWordModule
import kf.Word


object wRStack: IWordModule {
    override val name = "kf.words.core.RStack"
    override val description = "Working with the return stack."

    override val words = arrayOf(
        Word(">R", ::w_toR),
        Word("R>", ::w_rFrom),
        Word("R@", ::w_rFetch),
    )



    /**  `>R` ( n -- R:n ) Move top of dstk to rstk */
    fun w_toR(vm: ForthVM) {
        vm.rstk.push(vm.dstk.pop())
    }

    /**  `R>`( r:n -- n ) Move top of rstk to dstk  */
    fun w_rFrom(vm: ForthVM) {
        vm.dstk.push(vm.rstk.pop())
    }

    /** `R@ ( -- x ) ( R: x -- x ) Copy x from the rstk to dstk */
    fun w_rFetch(vm: ForthVM) {
        vm.dstk.push(vm.rstk.peek())
    }


}


