package kf.consoles

import kf.ForthVM
import kf.interfaces.IConsole
import kf.interfaces.IForthVM
import org.jline.utils.AttributedStyle

/** A terminal interface for testing & gateways: it reads input from the
 * internal list of strings. These can be refilled.
 */
abstract class ConsoleBase() : IConsole {
    open fun setUp(vm: ForthVM) {}
}