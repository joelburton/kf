/** Terminal interfaces for non-standard terminals:
 *
 * - testing / gateways
 * - file input
 */

package kf

import com.github.ajalt.mordant.platform.MultiplatformSystem
import com.github.ajalt.mordant.terminal.StandardTerminalInterface
import com.github.ajalt.mordant.terminal.TerminalInterface
import com.github.ajalt.mordant.terminal.TerminalRecorder

/** This is a global singleton; it holds the output of any recorder terminal,
 * like the TerminalTestInterface, below.
 */
val recorder = TerminalRecorder()

/** A terminal interface for testing & gateways: it reads input from the
 * internal list of strings. These can be refilled.
 */
class TerminalTestInterface : TerminalInterface by recorder  {
    val inputs: MutableList<String> = mutableListOf()

    fun addInputs(vararg cmds: String) {
        for (cmd in cmds) inputs.add(cmd)
    }

    override fun readLineOrNull(hideInput: Boolean) = inputs.removeFirstOrNull()
}

/** A terminal interface from reading from files. THe files are read up front,
 * and the "terminal" gets lines from the cached file input.
 */
class TerminalFileInterface(path: String) : StandardTerminalInterface()  {
    val content: String

    init {
        val possibleContent = MultiplatformSystem.readFileAsUtf8(path)
        if (possibleContent == null) throw FileError("$path couldn't be read")
        content = possibleContent
    }

    val inputs: MutableList<String> = content.split("\n").toMutableList()
    override fun readLineOrNull(hideInput: Boolean) = inputs.removeFirstOrNull()
}

/** A terminal interface for strings. */

class TerminalStringInterface(content: String) : StandardTerminalInterface()  {
    val inputs: MutableList<String> = content.split("\n").toMutableList()
    override fun readLineOrNull(hideInput: Boolean) = inputs.removeFirstOrNull()
}

