package kf.interfaces

interface ISource {
    val vm: IForthVM
    val id: Int
    val path: String
    var ptr: Int
    var lineCount: Int
    var storedInPtr: Int
    val scanner: IFScanner
    fun readLineOrNull(): String?
    fun pop()
    fun push(newSrc :ISource)
}