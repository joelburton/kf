package kf.words.doublenums

import kf.IWordClass
import kf.Word

object mDoubleNums: IWordClass {
    override val name = "Double"
    override val description = "Double words"
    override val words = arrayOf<Word>(
        *wDoubleNums.words,
    )
}