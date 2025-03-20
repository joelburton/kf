package kf.words.core.ext

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wCaseExt: IWordClass {
    override val name = "core.ext.caseExt"
    override val description = "Case"

    override val words
        get() = arrayOf(
            Word("CASE", ::w_notImpl),
            Word("ENDCASE", ::w_notImpl),
            Word("ENDOF", ::w_notImpl),
            Word("OF", ::w_notImpl),
        )


}