package kf.consoles

import kf.ForthVM
import kf.ParseError
import org.jline.reader.EndOfFileException
import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.jline.utils.InfoCmp
import org.jline.utils.ShutdownHooks
import org.jline.utils.Status
import org.jline.widget.AutosuggestionWidgets

/** Terminal interfaces for non-standard terminals:
 *
 * - testing / gateways
 * - file input
 */
class ForthConsole(val vm: ForthVM) : IForthConsole {

    /** LineReader that doesn't print newline after accepting. */
    class ForthLineReader(vm: ForthVM, terminal: Terminal?) :
        LineReaderImpl(terminal, "kf") {
        init {
            completer = ForthCompleter(vm.dict)
            highlighter = ForthHighlighter(vm)
            AutosuggestionWidgets(this).enable()
            setVariable(
                HISTORY_FILE,
                "${System.getProperty("user.home")}/.kf_history"
            )
        }

        override fun cleanup() = doCleanup(false)
    }

    override val termWidth get() = (if (term.width == 0) 80 else term.width) - 1
    private val term: Terminal = TerminalBuilder.builder().dumb(true).build()
        .also { it.enterRawMode() }
    private val reader: ForthLineReader by lazy { ForthLineReader(vm, term) }
    private var status: Status? =
        Status.getStatus(term, true)?.apply { setBorder(true) }

    init {
        ShutdownHooks.add(ShutdownHooks.Task {
            reader.history.save()
            term.close()
        })
    }

    private fun mks(s: String, style: AttributedStyle) =
        AttributedString(s, style).toAnsi(term)

    private fun nl(s: String) =
        pl("${if (term.getCursorPosition(null).x != 0) "\n" else ""}$s")

    private fun p(s: String) = term.writer().print(s)
    private fun pl(s: String) = term.writer().println(s)

    // API

    override fun keyAvail() = System.`in`.available()
    override fun readKey() = term.reader().read()
    override fun readLine(): String? {
        if (status != null) updateStatusBar()
        return try {
            reader.readLine()
        } catch (_: EndOfFileException) {
            null
        }
    }

    override fun showHistory() {
        reader.history.forEach {
            val n = (it.index() + 1).toString().padStart(3)
            vm.io.println("$n: ${it.line()}")
        }
    }

    override fun runFromHistory(prev: Int): String? {
        return try {
            reader.history[prev.toInt()]
        } catch (_: IllegalArgumentException) {
            null
        }

    }

    override fun termInfo() = println("${term.type} width=${term.width}")
    override fun setXY(x: Int, y: Int) {
        term.puts(InfoCmp.Capability.cursor_address, y, x);
    }

    override fun clearScreen() {
        term.puts(InfoCmp.Capability.clear_screen)
        term.flush()
    }

    // standard print

    override fun print(s: String) = p(s)
    override fun print(s: String, style: AttributedStyle) = p(mks(s, style))
    override fun println(s: String) = pl(s)
    override fun println(s: String, style: AttributedStyle) = pl(mks(s, style))

    // print a line stylized (all start on new line except "ok")

    override fun out(s: String) = p(mks(s, AttributedStyle.DEFAULT.italic()))
    override fun info(s: String) = nl(
        mks(
            s, AttributedStyle.DEFAULT.foreground(
                AttributedStyle.YELLOW
            )
        )
    )

    override fun danger(s: String) = nl(
        mks(
            s, AttributedStyle.DEFAULT.foreground(
                AttributedStyle.RED
            )
        )
    )

    override fun muted(s: String) =
        nl(mks(s, AttributedStyle.DEFAULT.foreground(247)))

    override fun warning(s: String) =
        nl(mks(s, AttributedStyle.DEFAULT.foreground(208)))

    override fun success(s: String) =
        nl(mks(s, AttributedStyle.DEFAULT.foreground(120).bold()))

    override fun ok(s: String) =
        pl(mks(s, AttributedStyle.DEFAULT.foreground(120).bold()))

    override fun debug(s: String) =
        nl(mks(s, AttributedStyle.DEFAULT.foreground(241)))

    override fun debugSubtle(s: String) =
        nl(mks(s, AttributedStyle.DEFAULT.foreground(245)))

    private fun updateStatusBar() {
        val stk = vm.dstk.simpleDumpStr()
        val mode = if (vm.interp.isCompiling) " Compiling " else ""
        val base = when (vm.base) {
            2 -> "Bin "
            8 -> "Oct "
            10 -> "Dec "
            16 -> "Hex "
            else -> "Base(${vm.base}) "
        }
        val toPad = vm.io.termWidth - (stk.length + base.length + mode.length)
        val padding = " ".repeat(if (toPad > 0) toPad else 0)

        val info = AttributedStringBuilder()
            .append(AttributedString(stk, AttributedStyle.DEFAULT.bold()))
            .append(padding)
            .append(
                AttributedString(
                    base,
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)
                )
            )
            .append(
                AttributedString(
                    mode,
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA)
                )
            )
            .toAttributedString()
        status?.update(mutableListOf(info))
    }
}