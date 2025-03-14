package kf

import com.github.ajalt.mordant.platform.MultiplatformSystem
import com.github.ajalt.mordant.terminal.StandardTerminalInterface
import com.github.ajalt.mordant.terminal.TerminalInterface
import com.github.ajalt.mordant.terminal.TerminalRecorder

val recorder = TerminalRecorder()

class TerminalTestInterface : TerminalInterface by recorder  {
    val inputs: MutableList<String> = mutableListOf()

    fun addInputs(vararg cmds: String) {
        for (cmd in cmds) inputs.add(cmd)
    }

    override fun readLineOrNull(hideInput: Boolean) = inputs.removeFirstOrNull()
}


class TerminalFileInterface(val path: String) : StandardTerminalInterface()  {
    val content = MultiplatformSystem.readFileAsUtf8(path)!!
    val inputs: MutableList<String> = content.split("\n").toMutableList()
    override fun readLineOrNull(hideInput: Boolean) = inputs.removeFirstOrNull()
}
