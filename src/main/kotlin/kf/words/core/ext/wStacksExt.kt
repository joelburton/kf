package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord


object wStacksExt: IWordModule {
    override val name = "kf.words.core.ext.wRStackExt"
    override val description = "Handling calling and exiting custom functions"

    override val words: Array<IWord> = arrayOf(
        Word("2>R", ::w_twoToR),
        Word("2R>", ::w_twoRFrom),
        Word("2R@", ::w_twoRFetch),
    )

    /**  `2>R` ( n -- r:n : move top of dstk to rstk ) */
    fun w_twoToR(vm: IForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.rstk.push(x1, x2)
    }

    /** `2R>` ( -- x1 x2 ) ( R: x1 x2 -- ) */
    fun w_twoRFrom(vm: IForthVM) {
        val x2 = vm.rstk.pop()
        val x1 = vm.rstk.pop()
        vm.dstk.push(x1, x2)
    }

    /** `2R@` ( -- x1 x2 ) ( R: x1 x2 -- x1 x2 ) Copy x from rstk to the dstk */
    fun w_twoRFetch(vm: IForthVM) {
        val x2 = vm.rstk.peek()
        val x1 = vm.rstk.getFrom(1)
        vm.dstk.push(x1, x2)
    }


}


