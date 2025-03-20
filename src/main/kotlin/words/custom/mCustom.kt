package kf.words.custom

import kf.IWordClass
import kf.Word

object mCustom: IWordClass {
    override val name = "custom.Custom"
    override val description = "Custom words"
    override val words = arrayOf<Word>(
        *wBload.words,
        *wCharsCustom.words,
        *wCompilingCustom.words,
        *wCreateCustom.words,
        *wFunctionsCustom.words,
        *wInterpCustom.words,
        *wIOCustom.words,
        *wLogicCustom.words,
        *wMemoryCustom.words,
        *wNumIOCustom.words,
        *wRegisters.words,
        *wStacksCustom.words,
        *wTimeCustom.words,
        *wToolsCustom.words,
        *wWordsCustom.words,
    )
}