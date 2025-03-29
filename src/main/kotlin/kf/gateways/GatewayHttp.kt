package kf.gateways

import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kf.ForthVM


/**  HTTP gateway. */

class GatewayHttp(vm: ForthVM) : GatewayBase(vm) {
    override val server = embeddedServer(CIO, port = 8080) {
        routing {
            get("/") {
                call.respondText(run(call.receiveText()))
            }
        }
    }
}
