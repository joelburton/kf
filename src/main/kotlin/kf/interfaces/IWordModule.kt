package kf.interfaces

import kf.dict.Word

/** A class that contains the thematically-related words. */

interface IWordModule {
    val name: String
    val description: String
    val words: Array<Word>
}
