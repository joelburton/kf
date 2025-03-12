package kf

import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**  Gets input from commands it was originally created with, and collects
 * output rather than printing it to any type of console.
 *
 * Used for gateways or testing.
 */
class IOGateway : IOBase() {
    val bos = ByteArrayOutputStream()
    override val o: PrintStream = PrintStream(bos)
    override val err: PrintStream = o
    var input: MutableList<String> = mutableListOf()

    override fun readLine() = input.removeFirstOrNull()

    fun resetAndLoadCommands(cmds: String) {
        bos.reset()
        input = mutableListOf(*cmds.split("\n").toTypedArray())
    }

    fun getPrinted(): String = bos.toString()
}