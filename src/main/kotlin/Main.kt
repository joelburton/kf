package kf

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal

class Hello : CliktCommand("PupForth") {
    val verbosity: Int by option("-v", "--verbosity").int().default(0)
        .help("verbosity level (0 is default)")
    val plain: Boolean by option().flag().help("plain terminal output")
    val paths: List<String> by argument().multiple()
    val size: IMemConfig by option().switch(
        "--large" to LargeMemConfig,
        "--medium" to MedMemConfig,
        "--small" to SmallMemConfig,
    ).default(MedMemConfig).help("VM memory size (default is medium)")
    val gateway: String? by option().switch(
        "--http" to "http",
        "--websocket" to "websocket",
    ).help("gateway type (default is none)")

    fun executeFiles(vm: ForthVM) {
        for (path in paths) {
            vm.io = Terminal(terminalInterface = TerminalFileInterface(path))
            vm.verbosity = -2
            vm.reboot()
            try {
                vm.runVM()
            } catch (_: ForthEOF) {
            }
        }
    }

    override fun run() {
        val vm = ForthVM()

        if (gateway != null) {
            when (gateway) {
                "http" -> GatewayHttp(vm)
                else -> throw RuntimeException("Unknown gateway: $gateway")
            }.startGateway()
            return
        }

        val interactive = Terminal(
            ansiLevel = if (plain) AnsiLevel.NONE else null
        )

        try {
            executeFiles(vm)

            vm.io = interactive
            vm.verbosity = verbosity
            vm.reboot()
            vm.runVM()
        } catch (_: ForthEOF) {
            // just quit quietly
        } catch (_: ForthBye) {
            println("Bye")
        }
    }
}

fun main(args: Array<String>) = Hello().main(args)
