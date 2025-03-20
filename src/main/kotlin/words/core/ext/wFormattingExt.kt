package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wFormattingExt: IWordClass {
    override val name = "Formatting"
    override val description = "Picture words for numbers"

    override val words
        get() = arrayOf(
            Word("HOLDS", ::w_notImpl),
            )

}