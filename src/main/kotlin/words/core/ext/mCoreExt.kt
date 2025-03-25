package kf.words.core.ext

import kf.IMetaWordModule

object mCoreExt: IMetaWordModule {
    override val name = "kf.words.core.ext.mCoreExt"
    override val description = "Core Extensions"
    override val modules = arrayOf(
        wCaseExt,
        wCommentsExt,
        wCompileExt,
        wDeferExt,
        wFormattingExt,
        wInterpExt,
        wIOExt,
        wLogicExt,
        wLoopsExt,
        wMemoryExt,
        wNumIOExt,
        wParseExt,
        wStacksExt,
        wStackOpsExt,
        wStringsExt,
        wUnsignedExt,
        wValuesExt,
        wWordsExt,
    )
}

