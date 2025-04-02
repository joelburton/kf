package kf.words.core.ext

import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord
import kf.words.core.ext.wNumIOExt.w_dotR
import kf.words.core.wLogic.w_greaterThan

object wUnsignedExt: IWordModule {
    override val name = "kf.words.core.ext.wUnsignedExt"
    override val description = "Unsigned math"

    override val words: Array<IWord>
        get() = arrayOf(
            Word("U.R", ::w_dotR),
            Word("U>", ::w_greaterThan),
        )
}