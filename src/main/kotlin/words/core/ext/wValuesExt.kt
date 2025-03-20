package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wValuesExt: IWordClass {
    override val name = "core.ext.valuesExt"
    override val description = "Flexible variables"

    override val words
        get() = arrayOf<Word>(
            Word("VALUE", ::w_notImpl),
            Word("TO", ::w_notImpl)
        )
}