package kf.interfaces

interface IWord {
    val name: String
    var fn: (IForthVM) -> Unit
    var cpos: Int
    var dpos: Int
    var hidden: Boolean
    var imm: Boolean
    val compO: Boolean
    val interpO: Boolean
    var recursive: Boolean
    var deferToWn: Int?
    var wn: Int
}