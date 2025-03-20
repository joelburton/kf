package kf.words.fileaccess

import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wFileAccess : IWordClass {
    override val name = "File Access"
    override val description = "File Access"
    override val words = arrayOf<Word>(
        Word(" BIN", ::w_notImpl),
        Word("CLOSE-FILE", ::w_notImpl),
        Word("CREATE-FILE", ::w_notImpl),
        Word("DELETE-FILE", ::w_notImpl),
        Word("FILE-POSITION", ::w_notImpl),
        Word("FILE-SIZE", ::w_notImpl),
        Word("INCLUDE-FILE", ::w_notImpl),
        Word("INCLUDED", ::w_notImpl),
        Word("OPEN-FILE", ::w_notImpl),
        Word("R/O", ::w_notImpl),
        Word("R/W", ::w_notImpl),
        Word("READ-FILE", ::w_notImpl),
        Word("READ-LINE", ::w_notImpl),
        Word("REPOSITION-FILE", ::w_notImpl),
        Word("RESIZE-FILE", ::w_notImpl),
//Word("S\"", ::w_notImpl),
        Word("SOURCE-ID", ::w_notImpl),
        Word("W/O", ::w_notImpl),
        Word("WRITE-FILE", ::w_notImpl),
        Word("WRITE-LINE", ::w_notImpl),

        )

}