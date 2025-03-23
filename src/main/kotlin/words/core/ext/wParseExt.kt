package kf.words.core.ext

import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wParseExt: IWordModule {
    override val name = "kf.words.core.ext.wParseExt"
    override val description = "Input/Output"

    override val words = arrayOf<Word>(
        Word("PARSE", ::w_notImpl)
    )
}