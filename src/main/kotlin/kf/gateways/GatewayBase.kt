package kf.gateways

import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kf.*
import kf.consoles.RecordingForthConsole
import kf.sources.SourceFakeInteractive

abstract class GatewayBase(val vm: ForthVM) {
    val io = RecordingForthConsole()
    abstract val server: EmbeddedServer<CIOApplicationEngine,
            CIOApplicationEngine.Configuration>

    fun start() {
        vm.io = io
        println("Starting gateway... control-c to quit")
        server.start(wait = true)
    }

    fun stop() {
        server.stop()
    }

    fun run(code: String): String {
        println(code)
        (vm.source as SourceFakeInteractive).content = code

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
                    println("Interrupt: $e")
                    io.output
                }
            }
        } finally {
            println(io.output)
            io.clear()
        }
    }
}

