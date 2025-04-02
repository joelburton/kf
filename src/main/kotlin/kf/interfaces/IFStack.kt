package kf.interfaces

import kotlin.time.TimeSource

interface IFStack {
    val vm: IForthVM
    val name: String
    val startAt: Int
    val endAt: Int
    var sp: Int
    val size: Int
    fun asArray(): IntArray

    fun getAt(n: Int): Int
    fun getFrom(n:Int): Int
    fun popFrom(n: Int): Int
    fun push(value: Int)
    fun push(a: Int, b: Int)
    fun push(vararg values: Int)
    fun dblPush(n: Long)
    fun dblPop(): Long
    fun pop(): Int
    fun peek(): Int
    fun simpleDumpStr(): String
    fun simpleDump()
    fun dump()
}