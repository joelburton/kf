package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wIOExt: IWordClass {
    override val name = "IOExt"
    override val description = "Input/Output"

    override val words = arrayOf<Word>(
        Word("SOURCE-ID", ::w_notImpl),
        Word("RESTORE-INPUT", ::w_notImpl),
        Word("SAVE-INPUT", ::w_notImpl),
    )
}