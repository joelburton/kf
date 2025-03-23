package kf.words.core.ext

import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wValuesExt: IWordModule {
    override val name = "kf.words.core.ext.wValuesExt"
    override val description = "Flexible variables"

    override val words
        get() = arrayOf<Word>(
            Word("VALUE", ::w_notImpl),
            Word("TO", ::w_notImpl)
        )
}