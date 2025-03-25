/** Terminal interfaces for non-standard terminals:
 *
 * - testing / gateways
 * - file input
 */

package kf

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

    // fixme: since the input system was decoupled from the output system,
    //  this should probably go away and the gateways should use an InputSource.
    fun addInputs(vararg cmds: String) {
        for (cmd in cmds) inputs.add(cmd)
    }

    override fun readLineOrNull(hideInput: Boolean) = inputs.removeFirstOrNull()
}
