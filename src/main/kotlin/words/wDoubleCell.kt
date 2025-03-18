package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wDoubleCell: IWordClass {
    override val name = "DoubleCell"
    override val description = "Double-cell words"

    override val words
        get() = arrayOf(
            Word("S>D", ::w_notImpl),
            Word("M*", ::w_mStar),
        )

    /** M*   m-star  CORE
     *
     * ( n1 n2 -- d )
     *
     * d is the signed product of n1 times n2.
     */

    fun w_mStar(vm: ForthVM) {
        val n2 = vm.dstk.pop().toLong()
        val n1 = vm.dstk.pop().toLong()
        val product = n1 * n2
//        vm.dstk.push(n1 * n2)   fixme
    }

    /** S>D  s-to-d  CORE
     *
     * ( n -- d )
     *
     * Convert the number n to the double-cell number d with the same numerical
     * value.
     */

}