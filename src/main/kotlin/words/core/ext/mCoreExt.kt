package kf.words.core.ext

import kf.IWordClass

object mCoreExt: IWordClass {
    override val name = "core.ext.CoreExt"
    override val description = "Core Extensions"
    override val words = arrayOf(
        *wCaseExt.words,
        *wCommentsExt.words,
        *wCompileExt.words,
        *wDeferExt.words,
        *wFormattingExt.words,
        *wInterpExt.words,
        *wIOExt.words,
        *wLogicExt.words,
        *wLoopsExt.words,
        *wMemoryExt.words,
        *wNumIOExt.words,
        *wParseExt.words,
        *wRStackExt.words,
        *wStackOpsExt.words,
        *wStringsExt.words,
        *wUnsignedExt.words,
        *wValuesExt.words,
        *wWordsExt.words,
    )
}

