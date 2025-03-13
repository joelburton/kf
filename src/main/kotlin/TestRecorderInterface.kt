package kf

import com.github.ajalt.mordant.terminal.TerminalInterface
import com.github.ajalt.mordant.terminal.TerminalRecorder

val recorder = TerminalRecorder()

class TerminalTestInterface : TerminalInterface by recorder  {
    val inputs: MutableList<String> = mutableListOf()

    fun addInputs(vararg cmds: String) {
        for (cmd in cmds) inputs.add(cmd)
    }

    override fun readLineOrNull(hideInput: Boolean): String? {
        return inputs.removeFirstOrNull()
    }
}



