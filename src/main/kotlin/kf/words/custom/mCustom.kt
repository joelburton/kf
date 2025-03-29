package kf.words.custom

import kf.dict.IWordMetaModule

object mCustom: IWordMetaModule {
    override val name = "kf.words.custom.mCustom"
    override val description = "Custom words"
    override val modules = arrayOf(
        wBload,
        wCharsCustom,
        wCompilingCustom,
        wFunctionsCustom,
        wInterpCustom,
        wIOCustom,
        wLogicCustom,
        wMemoryCustom,
        wNumIOCustom,
        wRegisters,
        wStacksCustom,
        wTimeCustom,
        wToolsCustom,
        wWordsCustom,
    )
}