package kf

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import kf.interps.InterpBase
import kf.interps.InterpEval
import kf.interps.InterpFast
import kf.interps.InterpForth

class ForthCLI : CliktCommand("PupForth") {

    val verbosity: Int by option("-v", "--verbosity").int()
        .default(1)
        .help("verbosity level (default: 1)")

    val ansiLevel: AnsiLevel? by option()
        .switch(
            "--ansi" to AnsiLevel.TRUECOLOR,
            "--dumb" to AnsiLevel.NONE,
        )
        .help("terminal type (default: detect)")

    val paths: List<String> by argument()
        .multiple()

    val size: IMemConfig by option()
        .switch(
            "--large" to LargeMemConfig(),
            "--medium" to MedMemConfig(),
            "--small" to SmallMemConfig(),
        )
        .default(MedMemConfig())
        .help("VM memory size (default: medium)")

    val gateway: String? by option()
        .switch(
            "--http" to "http",
            "--websocket" to "websocket",
        )
        .help("gateway type (default: none)")

    val listMemConfigs: Boolean by option("--list-mem-configs")
        .flag(default = false)
        .help("list available memory configurations")

    val version: Boolean by option("-V", "--version")
        .flag(default = false)
        .help("show version")

    val interp: String by option()
        .switch(
            "--base" to "base",
            "--eval" to "eval",
            "--fast" to "fast",
            "--forth" to "forth",
        )
        .default("fast")
        .help("interpreter to use")

    fun runFiles(vm: ForthVM) {
        for (path in paths) {
            print("Running $path ... ")
            vm.io = Terminal(terminalInterface = TerminalFileInterface(path))
            vm.verbosity = -2
            try {
                vm.runVM()
            } catch (e: Interrupt) {
                when (e) {
                    is IntEOF -> continue  // next file
                    is IntQuit -> break  // move to interactive
                    else -> throw e
                }
            }
        }
    }

    fun wrappedRun() {
        if (version) {
            println(VERSION_STRING)
            return
        }
        if (listMemConfigs) {
            for (memConfig in memoryConfigs) {
                println("${memConfig.name}:")
                memConfig.show()
                println()
            }
            return
        }

        val vm = ForthVM(memConfig = size)
        val theInterp = when (interp) {
            "base" -> InterpBase(vm)
            "eval" -> InterpEval(vm)
            "fast" -> InterpFast(vm)
            "forth" -> InterpForth(vm)
            else -> throw RuntimeException("Unknown interpreter: $interp")
        }
        vm.interp = theInterp

        vm.verbosity = verbosity
        vm.reboot()

        // first, process any files passed in on cmd line
        runFiles(vm)

        // start a gateway if one given
        if (gateway != null) {
            when (gateway) {
                "websocket" -> GatewayWebsocket(vm)
                "http" -> GatewayHttp(vm)
                else -> throw RuntimeException("Unknown gateway: $gateway")
            }.start()
            return
        }

        vm.io = Terminal(ansiLevel = ansiLevel)
        vm.verbosity = verbosity
        vm.runVM()
    }

    override fun run() {
        try {
            wrappedRun()
        } catch (_: IntEOF) {
            // just quit quietly
        } catch (e: IntBye) {
            println(e.message)
        }
    }
}

fun main(args: Array<String>) = ForthCLI().main(args)
