package kf

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal

class Hello : CliktCommand() {
    val plainTerm: Boolean by option().flag()
        .help("Use plain terminal output")

    override fun run() {
        val term = if (plainTerm)
            Terminal(ansiLevel = AnsiLevel.NONE)
        else
            Terminal()
        val vm = ForthVM(io=term)
        vm.reboot()
        vm.runVM()
    }
}

fun main(args: Array<String>) = Hello().main(args)

//fun main(args: Array<String>) {
////    val io = IOAnsiConsole()
////    val io = IOTerminal()
//    val vm = ForthVM()
//    vm.reboot()
//    vm.runVM()
//}
