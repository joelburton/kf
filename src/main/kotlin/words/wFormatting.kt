package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.numToStr
import kf.w_notImpl

object wFormatting: IWordClass {
    override val name = "Formatting"
    override val description = "Picture words for numbers"

    override val words
        get() = arrayOf(
            Word("#", ::w_notImpl),
            Word("#>", ::w_notImpl),
            Word("#S", ::w_notImpl),
            Word("<#", ::w_notImpl),
            Word("HOLD", ::w_notImpl),
            Word(".", ::w_dot),
            Word("SIGN", ::w_notImpl),

        )

    /**
     * 6.1.2210
     * SIGN
     * CORE
     * ( n -- )
     * If n is negative, add a minus sign to the beginning of the pictured numeric output string. An ambiguous condition exists if SIGN executes outside of a <# #> delimited number conversion.
     *
     */

    /** .   dot     CORE
     *
     * ( n -- )
     *
     * Display n in free field format.
     */

    fun w_dot(vm: ForthVM) {
        vm.io.print("${vm.dstk.pop().numToStr(vm.base)} ")
    }
}