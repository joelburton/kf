package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wStringsExt: IWordClass {
    override val name = "core.ext.stringsExt"
    override val description = "Strings"

    override val words
        get() = arrayOf(
            Word("C\"", ::w_notImpl),
            Word("S\\\"", ::w_notImpl),
        )
}
