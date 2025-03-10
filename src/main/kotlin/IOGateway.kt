package kf

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.CharArrayReader
import java.io.PrintStream

/**  Gets input from commands it was originally created with, and collects
 * output rather than printing it to any type of console.
 *
 * Used for gateways or testing.
 */
class IOGateway : IOBase() {
    val bos = ByteArrayOutputStream()
    override val output: PrintStream = PrintStream(bos)
    override val err: PrintStream = output
    override var input: BufferedReader? = null

    fun resetAndLoadCommands(cmds: String) {
        bos.reset()
        input = BufferedReader(CharArrayReader(cmds.toCharArray()))
    }

    fun getPrinted(): String = bos.toString()
}