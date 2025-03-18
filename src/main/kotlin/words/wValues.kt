package kf.words

import kf.IWordClass
import kf.Word

object wValues: IWordClass {
    override val name = "Values"
    override val description = "Flexible variables"

    override val words
        get() = arrayOf<Word>(
        )
}