package kf.gateways

import io.ktor.server.application.install
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kf.ForthVM


/** Websockets gateway. */

class GatewayWebsocket(vm: ForthVM) : GatewayBase(vm) {
    override val server = embeddedServer(CIO, port = 8080) {
        install(WebSockets)
        routing {
            webSocket("/ws") { // websocketSession
                for (frame in incoming) {
                    val cmdString = (frame as Frame.Text).readText()
                    val output = run(cmdString)
                    send(Frame.Text(output))
                }
            }
        }
    }
}

