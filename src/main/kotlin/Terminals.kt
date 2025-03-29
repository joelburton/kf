/** Terminal interfaces for non-standard terminals:
 *
 * - testing / gateways
 * - file input
 */

package kf

import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.jline.utils.AttributedStyle.BLACK
import org.jline.utils.AttributedStyle.BLUE
import org.jline.utils.AttributedStyle.RED
import org.jline.utils.AttributedStyle.GREEN
import org.jline.utils.AttributedStyle.YELLOW
import org.jline.utils.Colors


/** This is a global singleton; it holds the output of any recorder terminal,
 * like the TerminalTestInterface, below.
 */
val recorder = ""

interface IOutputSource {
    val termWidth: Int
    val terminal: Terminal?
    fun out(s: String)
    fun print(s: String)
    fun println(s: String = "")
    fun error(s: String)
    fun errorln(s: String = "")
    fun print(s: String, style: AttributedStyle)
    fun println(s: String, style: AttributedStyle)
    fun error(s: String, style: AttributedStyle)
    fun errorln(s: String, style: AttributedStyle)
    fun info(s: String)
    fun danger(s: String)
    fun muted(s: String)
    fun warning(s: String)
    fun success(s: String)
    fun debug(s: String)
    fun debugSubtle(s: String)
}


class TerminalOutputSource() : IOutputSource {
    override val termWidth
        get() =
            (if (terminal.width == 0) 80 else terminal.width) - 1
    override val terminal: Terminal =
        TerminalBuilder.builder().dumb(true).build()

    fun makeStr(s: String, style: AttributedStyle) =
        AttributedString(s, style).toAnsi(terminal)

    override fun out(s: String) =
        kotlin.io.print(makeStr(s, AttributedStyle.DEFAULT.italic()))

    override fun print(s: String) = kotlin.io.print(s)
    override fun println(s: String) = kotlin.io.println(s)
    override fun error(s: String) = System.err.print(s)
    override fun errorln(s: String) = System.err.println(s)

    override fun print(s: String, style: AttributedStyle) =
        kotlin.io.print(makeStr(s, style))

    override fun println(s: String, style: AttributedStyle) =
        kotlin.io.println(makeStr(s, style))

    override fun error(s: String, style: AttributedStyle) =
        System.err.print(makeStr(s, style))

    override fun errorln(s: String, style: AttributedStyle) =
        System.err.print(makeStr(s, style))

    override fun info(s: String) =
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(GREEN)))

    override fun danger(s: String) =
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(RED)))

    override fun muted(s: String) =
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(247)))

    override fun warning(s: String) =
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(208)))

    override fun success(s: String) =
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(BLUE)))

    override fun debug(s: String) {
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(241)))
    }
    override fun debugSubtle(s: String) {
        kotlin.io.println(makeStr(s, AttributedStyle.DEFAULT.foreground(245)))
    }
}


/** A terminal interface for testing & gateways: it reads input from the
 * internal list of strings. These can be refilled.
 */
class TestTerminalOutputSource : IOutputSource {
    override val terminal = null
    override val termWidth = 80

    val outputList = mutableListOf<String>()

    override fun out(s: String) {
        outputList.add(s)
    }

    override fun print(s: String) {
        outputList.add(s)
    }

    override fun println(s: String) {
        outputList.add("$s\n")
    }

    override fun error(s: String) {
        outputList.add(s)
    }

    override fun errorln(s: String) {
        outputList.add("$s\n")
    }

    override fun print(s: String, style: AttributedStyle) {
        outputList.add(s)
    }

    override fun println(s: String, style: AttributedStyle) {
        outputList.add("$s\n")
    }

    override fun error(s: String, style: AttributedStyle) {
        outputList.add(s)
    }

    override fun errorln(s: String, style: AttributedStyle) {
        outputList.add("$s\n")
    }

    override fun info(s: String) {
        outputList.add("$s\n")
    }

    override fun danger(s: String) {
        outputList.add("$s\n")
    }

    override fun muted(s: String) {
        outputList.add("$s\n")
    }

    override fun success(s: String) {
        outputList.add("$s\n")
    }

    override fun warning(s: String) {
        outputList.add("$s\n")
    }

    override fun debug(s: String) {
        outputList.add("$s\n")
    }

    override fun debugSubtle(s: String) {
        outputList.add("$s\n")
    }

    val output get() = outputList.joinToString("")
    fun clear() {
        outputList.clear()
    }
}
