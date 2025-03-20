package kf.words.core

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wUnsigned: IWordClass {
    override val name = "Unsigned"
    override val description = "Unsigned math"

    override val words
        get() = arrayOf(
            Word("U.", ::w_notImpl),
            Word("UM*", ::w_notImpl),
            Word("UM/MOD", ::w_notImpl),
            Word("U<", ::w_notImpl),
        )
}