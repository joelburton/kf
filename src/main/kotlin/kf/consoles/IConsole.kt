package kf.consoles

import kf.ForthVM

/** This is a global singleton; it holds the output of any recorder terminal,
 * like the TerminalTestInterface, below.
 */

interface IConsole {
    val termWidth: Int

    // API
    fun setUp(vm: ForthVM)
    fun keyAvail(): Int
    fun readKey(): Int
    fun readLine(): String?
    fun setXY(x: Int, y: Int)
    fun clearScreen()
    fun termInfo()
    fun showHistory() // todo: move out of console
    fun runFromHistory(prev: Int): String? // ^

    // Standard print
    fun print(s: String)
    fun println(s: String = "")

    // Print stylized line (all start on new line except "ok")
    fun out(s: String)
    fun info(s: String)
    fun danger(s: String)
    fun muted(s: String)
    fun warning(s: String)
    fun success(s: String)
    fun bold(s: String)
    fun ok(s: String)
    fun debug(s: String)
    fun debugSubtle(s: String)
}