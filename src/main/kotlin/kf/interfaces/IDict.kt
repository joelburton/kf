package kf.interfaces

import kf.dict.Word

interface IDict {
    val vm: IForthVM
    val capacity: Int
    val words: List<Word>
    var currentlyDefining: Word?
    val size: Int
    val last: Word
    fun add(word: Word)
    operator fun get(wn: Int): Word
    operator fun get(name: String): Word
    fun getSafe(name: String): Word?
}