package kf.gateways

import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kf.*
import kf.consoles.RecordingForthConsole
import kf.sources.SourceFakeInteractive
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*

abstract class GatewayBase(val vm: ForthVM) {
    val thisTerm = Terminal()
    val io = RecordingForthConsole()
    abstract val server: EmbeddedServer<CIOApplicationEngine,
            CIOApplicationEngine.Configuration>

    fun start() {
        vm.io = io
        println(green(bold("\nStarting gateway... control-c to quit")))
        server.start(wait = true)
    }

    fun stop() {
        server.stop()
    }

    fun run(code: String): String {
        thisTerm.println(yellow(bold(code)))
        vm.sources.clear()
        vm.sources.add(SourceFakeInteractive(vm, code))
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

