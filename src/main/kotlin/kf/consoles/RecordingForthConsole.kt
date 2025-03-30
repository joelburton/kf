package kf.consoles

import org.jline.utils.AttributedStyle

/** A terminal interface for testing & gateways: it reads input from the
 * internal list of strings. These can be refilled.
 */
class RecordingForthConsole() : IForthConsole {
    override val termWidth = 80

    val outputList = mutableListOf<String>()
    private fun add(s: String) {
        outputList.add(s)
    }

    // API
    override fun setUp(vm: kf.ForthVM) {}
    override fun keyAvail() = 0
    override fun readKey() = -1
    override fun readLine() = throw RuntimeException("Can't read w/o term")
    override fun setXY(x: Int, y: Int) {}
    override fun clearScreen() = println("\n".repeat(40))
    override fun termInfo() = println("test width=80")
    override fun showHistory() {}
    override fun runFromHistory(prev: Int) = null

    // standard print

    override fun print(s: String) = add(s)
    override fun print(s: String, style: AttributedStyle) = add(s)
    override fun println(s: String) = add("$s\n")
    override fun println(s: String, style: AttributedStyle) = add("$s\n")

    // print a line stylized (all start on new line except "ok")

    override fun out(s: String) = add(s)
    override fun info(s: String) = add("$s\n")
    override fun danger(s: String) = add("$s\n")
    override fun muted(s: String) = add("$s\n")
    override fun success(s: String) = add("$s\n")
    override fun ok(s: String) = add("$s\n")
    override fun warning(s: String) = add("$s\n")
    override fun debug(s: String) = add("$s\n")
    override fun debugSubtle(s: String) = add("$s\n")

    fun clear() = outputList.clear()
    val output get() = outputList.joinToString("")
}