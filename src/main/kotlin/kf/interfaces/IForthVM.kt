package kf.interfaces

import kf.mem.IMemConfig
import kotlin.time.TimeSource

interface IForthVM {
    val io: IConsole
    val interp: IInterp
    val memConfig: IMemConfig
    val mem: IntArray

    var base: Int
    var verbosity: Int
    var cend: Int
    var dend: Int
    var cstart: Int
    var dstart: Int
    var inPtr: Int

    val dict: IDict
    val cellMeta: Array<ICellMeta>
    val dstk: IFStack
    val rstk: IFStack

    var currentWord: IWord
    var ip: Int

    val sources: ArrayList<ISource>
    val source: ISource
    val timeMarkCreated: TimeSource.Monotonic.ValueTimeMark

    fun quit()
    fun abort()

    fun dbg(lvl: Int, s: String)

    fun appendWord(s: String)


}
