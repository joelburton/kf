package kf.words.core

import kf.ForthVM
import kf.IWordClass
import kf.Word


object wDoubleCell : IWordClass {
    override val name = "DoubleCell"
    override val description = "Double-cell words"

    override val words
        get() = arrayOf(
            Word("S>D", ::w_sToD),
            Word("M*", ::w_mStar),
        )

    /** M* ( n1 n2 -- d ) d is the signed product of n1 times n2. */

    fun w_mStar(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        val product = (n1.toLong() * n2.toLong())
        vm.dstk.dblPush(product)
    }

    /** S>D ( n -- d ) Convert n to double-cell. */

    fun w_sToD(vm: ForthVM) {
        vm.dstk.dblPush(vm.dstk.pop().toLong())
    }
}