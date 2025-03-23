package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word


object wRStackExt: IWordModule {
    override val name = "kf.words.core.ext.wRStackExt"
    override val description = "Handling calling and exiting custom functions"

    override val words = arrayOf(
        Word("2>R", ::w_twoToR),
        Word("2R>", ::w_twoRFrom),
        Word("2R@", ::w_twoRFetch),
    )



    /**  `2>R` ( n -- r:n : move top of dstk to rstk )
     */
    fun w_twoToR(vm: ForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.rstk.push(x1, x2)
    }

    /**  ( -- x1 x2 ) ( R: x1 x2 -- )
     */
    fun w_twoRFrom(vm: ForthVM) {
        val x2 = vm.rstk.pop()
        val x1 = vm.rstk.pop()
        vm.dstk.push(x1, x2)
    }

    /**
     * ( -- x1 x2 ) ( R: x1 x2 -- x1 x2 )
     * Copy x from the return stack to the data stack.
     */
    fun w_twoRFetch(vm: ForthVM) {
        val x2 = vm.rstk.peek()
        val x1 = vm.rstk.peek()
        vm.dstk.push(x1, x2)
    }


}


