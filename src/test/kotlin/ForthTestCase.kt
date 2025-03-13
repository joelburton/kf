import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import kf.CallableWord
import kf.ForthEOF
import kf.ForthVM
import kf.IOGateway
import kf.TerminalTestInterface
import kf.WTools
import kf.recorder
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

val dummyFunc: CallableWord = { it.ip = 2 }

open class ForthTestCase {
    val testIO = TerminalTestInterface()
    val vm = ForthVM(io = Terminal(
        ansiLevel = AnsiLevel.NONE,
        terminalInterface = testIO))

    init {
        vm.verbosity = -2
        vm.reboot()
    }

    fun eval(s: String): String {
        testIO.addInputs(s)
        with(vm) {
            try {
                while (true) {
                    val wn = mem[ip++]
                    dict[wn](this)
                }
            } catch (_: ForthEOF) {
                return recorder.output()
            }
        }
    }

    fun see(name: String) {
        WTools._see(vm, vm.dict[name], false)
        print(getOutput())
    }

    fun assertDStack(vararg items: Int) {
        assertContentEquals(items, vm.dstk.asArray())
        vm.dstk.reset()
    }

    fun assertDStackKeep(vararg items: Int) {
        assertContentEquals(items, vm.dstk.asArray())
    }

    fun assertPrinted(s: String) {
        assertEquals(s, getOutput())
    }

    fun getOutput(): String {
        return recorder.output().also {
            recorder.clearOutput()
        }
    }
}