package kf.interfaces

interface ISource {
    val vm: IForthVM
    val id: Int
    val path: String
    var ptr: Int
    var lineCount: Int
    var storedInPtr: Int
    fun readLineOrNull(): String?
    var scanner: IFScanner
    fun pop()
    fun push(source :ISource)
}