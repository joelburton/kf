package kf.interfaces

interface IFScanner {
    val start: Int
    val end: Int
    val size: Int
    var nChars: Int
    var tokIdx: Int
    var tokLen: Int
    val atEnd: Boolean
    fun curToken(): String
    fun fill(str: String)
    fun parseName(): Pair<Int, Int>
    fun parse(c: Char): Pair<Int, Int>
    fun wordParse(c: Char): Pair<Int, Int>
    fun nextLine()
}