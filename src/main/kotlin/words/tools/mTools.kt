package kf.words.tools

import kf.IWordClass
import kf.Word

object mTools: IWordClass {
    override val name = "Tools"
    override val description = "Tools"
    override val words = arrayOf<Word>(
        *wTools.words,
        *wToolsExt.words,
    )
}