package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wParseExt: IWordClass {
    override val name = "ParseExt"
    override val description = "Input/Output"

    override val words = arrayOf<Word>(
        Word("PARSE", ::w_notImpl)
    )
}