package kf.words.core

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule


object wDoubleCell : IWordModule {
    override val name = "kf.words.core.wDoubleCell"
    override val description = "Double-cell words"

    override val words
        get() = arrayOf<Word>(
            Word("S>D", ::w_sToD),
            Word("M*", ::w_mStar),
        )

    /** M* ( n1 n2 -- d ) d is the signed product of n1 times n2. */

    fun w_mStar(vm: IForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        val product = (n1.toLong() * n2.toLong())
        vm.dstk.dblPush(product)
    }

    /** S>D ( n -- d ) Convert n to double-cell. */

    fun w_sToD(vm: IForthVM) {
        vm.dstk.dblPush(vm.dstk.pop().toLong())
    }
}