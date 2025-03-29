package kf

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

abstract class ForthGateway(val vm: ForthVM) {
    val io = TestTerminalOutputSource()
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
        val cmds = code.split("\n").toTypedArray()
        // fixme: should this move to an input source?
//        termInterface.addInputs(*cmds)

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


/**  HTTP gateway. */

class GatewayHttp(vm: ForthVM) : ForthGateway(vm) {
    override val server = embeddedServer(CIO, port = 8080) {
        routing {
            get("/") {
                call.respondText(run(call.receiveText()))
            }
        }
    }
}


/** Websockets gateway. */

class GatewayWebsocket(vm: ForthVM) : ForthGateway(vm) {
    override val server = embeddedServer(CIO, port = 8080) {
        install(WebSockets)
        routing {
            webSocket("/ws") { // websocketSession
                for (frame in incoming) {
                    send(run((frame as Frame.Text).readText()))
                }
            }
        }
    }
}

/**  This is the only entry point we provide for the gateway:
 * it takes a string of input and calls the interpreter with it.
 * (This could be a simple line or many newline-joined lines).
 *
 * To make sure that each test block is isolated from each other,
 * the VM is rebooted in between calls to this.
 *
 * Normally "bye" would exit the program, but this catches bye
 * and just stops this particular interpreter run; it doesn't actually
 * stop the VM.
 *
 * It returns everything the VM output (out and err weaved together).
 */

