package kf.interfaces

typealias StaticFunc = (IForthVM) -> Unit

interface IWord {
    val name: String
    var fn: StaticFunc
    var cpos: Int
    var dpos: Int
    var hidden: Boolean
    var imm: Boolean
    var compO: Boolean
    var interpO: Boolean
    var recursive: Boolean
    var deferToWn: Int?
    var wn: Int

    operator fun invoke(vm: IForthVM)
    fun getHeaderStr(): String

}