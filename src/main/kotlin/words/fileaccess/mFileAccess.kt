package kf.words.fileaccess

import kf.IWordClass
import kf.Word

object mFileAccess: IWordClass {
    override val name = "File Access"
    override val description = "File Access"
    override val words = arrayOf<Word>(
        *wFileAccess.words,
        *wFileAccessExt.words,
    )
}