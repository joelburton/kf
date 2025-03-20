package kf.interps

import com.github.ajalt.mordant.terminal.success
import kf.D
import kf.FScanner
import kf.ForthVM
import kf.ForthVM.Companion.REG_STATE
import kf.VERSION_STRING
import kf.strFromAddrLen

/** Base for any "interpreter", even one that has no interpreter CLI at all. */

interface IInterp {
    val name: String
    val code: String
    fun resetInterpreter()
    fun addInterpreterCode()
    fun rebootInterpreter()
    fun getToken(): String
    val isInterpreting: Boolean
    val isCompiling: Boolean
    var state: Int
    var scanner: FScanner
    fun banner()
    fun _compile(token: String)
    fun _interpret(token: String)
    fun eval(line: String)
}


open class InterpBase(val vm: ForthVM) : IInterp {
    override val name: String = "Base"
    override val code: String = ""

    companion object {
        const val STATE_INTERPRETING: Int = 0
        const val STATE_COMPILING: Int = -1
    }

    // fixme: need this for now, since lots of words refer to it, even if they aren't words this would use

    /**  Scanner for reading and tokenizing input line. */
    override var scanner: FScanner = FScanner(
        vm, vm.memConfig.interpBufferStart, vm.memConfig.interpBufferEnd
    )

    override fun getToken(): String {
        if (D) vm.dbg(3, "getToken")
        val s = scanner.parseName().strFromAddrLen(vm)
//        if (s.length == 0) throw ParseError("Name expected")
        if (D) vm.dbg(3, "returning s: '$s'")
        return s
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
    override fun rebootInterpreter() {
        if (D) vm.dbg(3, "InterpBase.rebootInterpreter")

        // Put interpreter code in mem; the VM will start executing here
        addInterpreterCode()
        resetInterpreter()
        if (vm.verbosity > 0) banner()
    }

    /**  Handle a VM reset at the interpreter layer.
     */
    override fun resetInterpreter() {
        if (D) vm.dbg(3, "InterpBase.resetInterpreter")

        state = STATE_INTERPRETING
    }

    override fun addInterpreterCode() {
        vm.appendWord("(FOO)")
    }

    override fun banner() {
        vm.io.success("\nWelcome to ${VERSION_STRING} ($name)\n")
    }

    override fun eval(line: String) { TODO() }
    override fun _interpret(line: String) { TODO() }
    override fun _compile(line: String) { TODO() }
}
