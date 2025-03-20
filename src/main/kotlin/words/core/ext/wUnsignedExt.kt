package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wUnsignedExt: IWordClass {
    override val name = "core.ext.wUnsignedExt"
    override val description = "Unsigned math"

    override val words
        get() = arrayOf(
            Word("U.R", ::w_notImpl),
            Word("U>", ::w_notImpl),
        )
}