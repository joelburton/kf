package kf.words.core.ext

import kf.interfaces.IWordModule
import kf.dict.Word
import kf.dict.w_notImpl
import kf.interfaces.IWord

object wCaseExt: IWordModule {
    override val name = "kf.words.core.ext.wCaseExt"
    override val description = "Case"

    override val words: Array<IWord>
        get() = arrayOf(
            Word("CASE", ::w_notImpl),
            Word("ENDCASE", ::w_notImpl),
            Word("ENDOF", ::w_notImpl),
            Word("OF", ::w_notImpl),
        )


}