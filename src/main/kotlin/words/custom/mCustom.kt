package kf.words.custom

import kf.IMetaWordModule

object mCustom: IMetaWordModule {
    override val name = "kf.words.custom.mCustom"
    override val description = "Custom words"
    override val modules = arrayOf(
        wBload,
        wCharsCustom,
        wCompilingCustom,
        wCreateCustom,
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