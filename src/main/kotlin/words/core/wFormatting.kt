package kf.words.core

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wFormatting: IWordModule {
    override val name = "kf.words.core.wFormatting"
    override val description = "Picture words for numbers"

    override val words
        get() = arrayOf(
            Word("#", ::w_numberSign),
            Word("#>", ::w_numberSignGreater),
            Word("#S", ::w_numberSignS),
            Word("<#", ::w_lessNumberSign),
            Word("HOLD", ::w_hold),
            Word("SIGN", ::w_sign),
        )

    /**
     * 6.1.2210
     * SIGN
     * CORE
     * ( n -- )
     * If n is negative, add a minus sign to the beginning of the pictured numeric output string. An ambiguous condition exists if SIGN executes outside of a <# #> delimited number conversion.
     *
     */

    fun w_numberSign(vm: ForthVM) {}
    fun w_numberSignGreater(vm: ForthVM) {}
    fun w_numberSignS(vm: ForthVM) {}
    fun w_lessNumberSign(vm: ForthVM) {}
    fun w_hold(vm: ForthVM) {}
    fun w_sign(vm: ForthVM) {}
}