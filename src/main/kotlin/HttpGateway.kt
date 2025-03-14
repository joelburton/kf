package kf

import com.github.ajalt.mordant.terminal.Terminal
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress

interface ForthGateway {
    fun startGateway()
}

/**  Class for a "gateway" over HTTP.
 */
class GatewayHttp(val vm: ForthVM) : HttpHandler, ForthGateway {
    private val termInterface = TerminalTestInterface()
    private val server: HttpServer

    init {
        vm.io = Terminal(terminalInterface = termInterface)
        // This starts a separate thread for listening to incoming requests.
        // It will prevent the program from exiting (unless the gateway is
        // directly stopped, which we don't ever do). So, this runs forever
        // until a crashing error or control-c.
        try {
            server = HttpServer.create(InetSocketAddress(8000), 0)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        server.createContext("/", this)
    }

    override fun startGateway() {
        server.start()
        println("HTTP gateway started --- ctrl-c to quit")
    }

    @Throws(IOException::class)
    override fun handle(t: HttpExchange) {
        val incomingData = String(t.getRequestBody().readAllBytes())
        val cmds = incomingData.split("\n").toTypedArray()
        val response = runForthProgram(cmds)
        t.sendResponseHeaders(200, response.length.toLong())
        val os = t.getResponseBody()
        os.write(response.toByteArray())
        os.close()
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
    fun runForthProgram(cmds: Array<String>): String {
        termInterface.addInputs(*cmds)
        vm.reboot(true)
        System.out.printf("COMMANDS:\n%s\n\n", cmds)
        try {
            vm.runVM()
            return recorder.output()
        } catch (_: ForthBye) {
            return recorder.output()
        } catch (_: ForthEOF) {
            return recorder.output()
        } catch (e: ForthColdStop) {
            server.stop(0)
            throw e
        } finally {
            print("OUTPUT:\n${recorder.output()}\n\n\n")
        }
    }
}