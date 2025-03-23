package kf.words.core.ext

import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wStringsExt: IWordModule {
    override val name = "kf.words.core.ext.wStringsExt"
    override val description = "Strings"

    override val words
        get() = arrayOf(
            Word("C\"", ::w_notImpl),
            Word("S\\\"", ::w_notImpl),
        )
}
