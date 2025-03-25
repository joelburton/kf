package kf.interps

import com.github.ajalt.mordant.terminal.success
import kf.D
import kf.ForthVM
import kf.ForthVM.Companion.REG_STATE
import kf.IMetaWordModule
import kf.VERSION_STRING
import kf.words.mBaseInterp


/** A base interpreter.
 *
 * This doesn't support compilation or evaluation, and has no REPL interface.
 * It includes just enough to start up the VM and run code and handle
 * requests for input (`ACCEPT` and such).
 *
 * It's kind of like what an Embedded Forth system would offer.
 *
 * It is a subclass of the other interpreters.
 */

open class InterpBase(val vm: ForthVM) : IInterp {
    override val name: String = "Base"
    override val module: IMetaWordModule = mBaseInterp
    companion object {
        const val STATE_INTERPRETING: Int = 0
        const val STATE_COMPILING: Int = -1
    }

    /** A register for interpreter use: state of interpreting/compiling. */

    override var state: Int
        get() = vm.mem[REG_STATE]
        set(value) {
            vm.mem[REG_STATE] = value
        }

    override val isInterpreting get() = vm.mem[REG_STATE] == STATE_INTERPRETING
    override val isCompiling get() = vm.mem[REG_STATE] == STATE_COMPILING


    /**  Handle a VM reboot at the interpreter layer. */
    override fun reboot() {
        if (D) vm.dbg(3, "InterpBase.rebootInterpreter")

        addInterpreterCode()
        reset()
        if (vm.verbosity > 0) banner()
    }

    /**  Handle a VM reset at the interpreter layer. */
    override fun reset() {
        if (D) vm.dbg(3, "InterpBase.resetInterpreter")

        state = STATE_INTERPRETING
    }

    /** Code for the "interpreter".
     *
     * This can't compile anything and isn't intended to have any REPL.
     * So, this just has a single opcode poked into start-of-memory.
     * If you want to have a Forth experience that just runs your pre-written
     * code and has no other overhead, subclass this and have it poke in
     * your program.
     */
    override fun addInterpreterCode() {
        vm.appendWord("(FOO)") // just a silly word so we can test
    }

    /** Message shown on startup/reboot. */
    override fun banner() {
        vm.io.success("\nWelcome to ${VERSION_STRING} ($name)\n")
    }

    /** This would interpret a string token, but the base interpreter won't. */
    override fun interpret(token: String) {
        throw NotImplementedError("InterpBase cannot interpret")
    }

    /** This would compile a string token, but the base interpreter won't. */
    override fun compile(token: String) {
        throw NotImplementedError("InterpBase cannot compile")
    }
}
