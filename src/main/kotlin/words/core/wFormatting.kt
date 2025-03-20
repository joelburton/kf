package kf.words.core

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


}