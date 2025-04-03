package kf.interfaces

interface IFScanner {
    val start: Int
    val end: Int
    val size: Int
    var nChars: Int
    fun curToken(): String
    fun fill(str: String)
    fun parseName(): Pair<Int, Int>
    fun parse(term: Char): Pair<Int, Int>
    fun wordParse(term: Char): Pair<Int, Int>
    fun nextLine()
}