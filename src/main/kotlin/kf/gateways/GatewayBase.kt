package kf.gateways

import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kf.*
import kf.consoles.RecordingConsole
import kf.sources.SourceTestEvalInput
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*

abstract class GatewayBase(val vm: ForthVM) {
    val thisTerm = Terminal()
    val io = vm.io as RecordingConsole
    abstract val server: EmbeddedServer<CIOApplicationEngine,
            CIOApplicationEngine.Configuration>

    fun start() {
        println(green(bold("\nStarting gateway... control-c to quit")))
        server.start(wait = true)
    }

    fun stop() {
        server.stop()
    }

    fun run(code: String): String {
        thisTerm.println(yellow(bold(code)))
        vm.sources.clear()
        vm.sources.add(SourceTestEvalInput(vm, code))
        vm.ip = vm.cstart
        try {
            vm.runVM()
        } catch (e: ForthInterrupt) {
            return when (e) {
                is IntServerShutDown -> {
                    stop()
                    throw e
                }
                is IntEOF -> io.output
                else -> {
                    println(red("Interrupt: $e"))
                    io.output
                }
            }
        } finally {
            println(blue(italic(io.output)))
            io.clear()
        }
    }
}

