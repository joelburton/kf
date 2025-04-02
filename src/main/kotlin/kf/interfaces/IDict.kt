package kf.interfaces

interface IDict {
    val vm: IForthVM
    val capacity: Int
    val words: List<IWord>
    var currentlyDefining: IWord?
    val size: Int
    val last: IWord
    fun add(word: IWord)
    fun get(wn: Int): IWord
    fun get(name: String): IWord
    fun getSafe(name: String): IWord?
}