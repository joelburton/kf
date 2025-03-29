package kf.consoles

import org.jline.utils.AttributedStyle

/** This is a global singleton; it holds the output of any recorder terminal,
 * like the TerminalTestInterface, below.
 */

interface IForthConsole {
    val termWidth: Int

    // API
    fun keyAvail(): Int
    fun readKey(): Int
    fun readLine(): String?
    fun setXY(x: Int, y: Int)
    fun clearScreen()
    fun termInfo()
    fun showHistory()
    fun runFromHistory(prev: Int): String?

    // Standard print
    fun print(s: String)
    fun println(s: String = "")
    fun print(s: String, style: AttributedStyle)
    fun println(s: String, style: AttributedStyle)

    // Print stylized line (all start on new line except "ok")
    fun out(s: String)
    fun info(s: String)
    fun danger(s: String)
    fun muted(s: String)
    fun warning(s: String)
    fun success(s: String)
    fun ok(s: String)
    fun debug(s: String)
    fun debugSubtle(s: String)
}