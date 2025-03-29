package kf

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import kf.interps.InterpBase
import kf.interps.InterpEval
import kf.interps.InterpFast
import kf.interps.InterpForth
import kf.words.fileaccess.wFileAccessExt
import org.jline.terminal.TerminalBuilder

class ForthCLI : CliktCommand("PupForth") {

    /** How verbose should it be w/messages & debugging?
     *
     * See the [ForthVM.verbosity] property for a list of values.
     */

    val verbosity: Int by option("-v", "--verbosity").int()
        .default(1)
        .help("verbosity level (default: 1)")

    /** Should this use ANSI codes (colors, etc).
     *
     * This is normally detected, but it can be forced.
     */
//    val ansiLevel: AnsiLevel? by option()
//        .switch(
//            "--ansi" to AnsiLevel.TRUECOLOR,
//            "--dumb" to AnsiLevel.NONE,
//        )
//        .help("terminal type (default: detect)")

    /** List of paths it will INCLUDE as it starts up. */
    val paths: List<String> by argument()
        .multiple()

    /** The "small" size is very tiny, and is intended for just using less
     * memory for tests, etc. Medium is probably perfect.
     */
    val size: IMemConfig by option()
        .switch(
            "--large" to LargeMemConfig(),
            "--medium" to MedMemConfig(),
            "--small" to SmallMemConfig(),
        )
        .default(MedMemConfig())
        .help("VM memory size (default: medium)")

    /** This can serve Forth via HTTP or WebSockets. */
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

    /** Which interpreter to use? This is mostly for nerdy tinkering around. */
    val interp: String by option()
        .switch(
            "--base" to "base",
            "--eval" to "eval",
            "--fast" to "fast",
            "--forth" to "forth",
        )
        .default("fast")
        .help("interpreter to use")

    /** Load only essential modules (about 20% of the full word set).
     *
     * This is mostly for tinkering and building your own Forth :-)
     */
    val raw: Boolean by option("--raw")
        .flag(default = false)
        .help("Load only required modules for interp")


    /** The runner for all the options.
     *
     * It's "wrapped" only in the sense that the caller of this wraps its
     * call to this in a try/catch, and I don't want to have this big code
     * block indented in a huge-try catch around the whole thing.
     */
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

        // Build the ForthVM that everything from this point down uses.

        val vm = ForthVM(memConfig = size)

        // Attach the right interpreter to it.
        vm.interp = when (interp) {
            "base" -> InterpBase(vm)
            "eval" -> InterpEval(vm)
            "fast" -> InterpFast(vm)
            "forth" -> InterpForth(vm)
            else -> throw RuntimeException("Unknown interpreter: ${interp}")
        }

        vm.verbosity = verbosity
        vm.reboot(!raw)

        // Before the interactive mode starts, all files listed will be
        // read. This is done by adding each file as an input source.
        // This is done in reversed order as written on the command line:
        //
        //   ./kf a.fth b.fth c.fth
        //
        // should be processed in a -> b -> c -> interactive order,
        // but the list of sources is a stack, so these are pushed in reverse.
        // This DOESN'T run those files itself --- the VM doesn't do anything
        // until it's started once, below, with `vm.runVM()`

        for (path in paths.asReversed()) wFileAccessExt.include(vm, path)

        // Start a gateway if one given.
        //
        // It would be possible to start one or even both gateways AND have
        // an interactive experience at the same time. But I think this would
        // be confusing UX for users to understand. So, each gateway doesn't
        // release the main thread until shutdown, and this function returns
        // before getting to the interactive mode.

        if (gateway != null) {
            when (gateway) {
                "websocket" -> GatewayWebsocket(vm)
                "http" -> GatewayHttp(vm)
                else -> throw RuntimeException("Unknown gateway: $gateway")
            }.start()
            return
        }

        // Attach the right terminal (colors, etc.)
//        vm.io = TerminalOutputSource()

        // Start the puppy! It will run until the program quits --- and then it
        // will be very, very sad and will miss you.
        try {
            vm.runVM()
        } finally {
            vm.readerForHistory?.history?.save()
        }
    }

    /** Entry point for the CLI. */

    override fun run() {
//        val terminal = TerminalBuilder.builder().dumb(true).build()
//        println("${terminal.type} width=${terminal.width}")

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
