package kf.words.core.ext

import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wCaseExt: IWordModule {
    override val name = "kf.words.core.ext.wCaseExt"
    override val description = "Case"

    override val words
        get() = arrayOf(
            Word("CASE", ::w_notImpl),
            Word("ENDCASE", ::w_notImpl),
            Word("ENDOF", ::w_notImpl),
            Word("OF", ::w_notImpl),
        )


}