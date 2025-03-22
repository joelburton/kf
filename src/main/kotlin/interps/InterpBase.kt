package kf.interps

import com.github.ajalt.mordant.terminal.success
import kf.D
import kf.FScanner
import kf.ForthVM
import kf.ForthVM.Companion.REG_IN_PTR
import kf.ForthVM.Companion.REG_STATE
import kf.VERSION_STRING

/** Base for any "interpreter", even one that has no interpreter CLI at all. */

interface IInterp {
    val name: String
    fun reset()
    fun addInterpreterCode()
    fun reboot()
    val isInterpreting: Boolean
    val isCompiling: Boolean
    var state: Int
    fun banner()
    fun compile(token: String)
    fun interpret(token: String)
    fun eval(line: String)
}


open class InterpBase(val vm: ForthVM) : IInterp {
    override val name: String = "Base"

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


    /**  Handle a VM reboot at the interpreter layer.
     */
    override fun reboot() {
        if (D) vm.dbg(3, "InterpBase.rebootInterpreter")

        addInterpreterCode()
        reset()
        if (vm.verbosity > 0) banner()
    }

    /**  Handle a VM reset at the interpreter layer.
     */
    override fun reset() {
        if (D) vm.dbg(3, "InterpBase.resetInterpreter")

        state = STATE_INTERPRETING
        vm.scanner.reset()
    }

    override fun addInterpreterCode() {
        vm.appendWord("(FOO)") // just a silly word so we can test
    }

    override fun banner() {
        vm.io.success("\nWelcome to ${VERSION_STRING} ($name)\n")
    }

    override fun interpret(line: String) {
        throw NotImplementedError("InterpBase cannot interpret")
    }

    override fun compile(line: String) {
        throw NotImplementedError("InterpBase cannot compile")
    }

    override fun eval(line: String) {
        throw NotImplementedError("InterpBase cannot eval")
    }
}
