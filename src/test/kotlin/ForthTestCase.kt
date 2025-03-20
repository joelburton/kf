import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import kf.IntEOF
import kf.ForthVM
import kf.TerminalTestInterface
import kf.interps.InterpBase
import kf.words.custom.wToolsCustom._see
import kf.recorder
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun dummyFn(vm: ForthVM) { vm.ip = 2 }

open class ForthTestCase {
    val testIO = TerminalTestInterface()
    val vm = ForthVM(io = Terminal(
        ansiLevel = AnsiLevel.NONE,
        terminalInterface = testIO))

    init {
        vm.interp = InterpBase(vm)
        vm.verbosity = -2
        vm.reboot(false)  // raw :no mods except machine
    }

    fun eval(s: String): String {
        testIO.addInputs(s)
        with(vm) {
            try {
                while (true) {
                    val wn = mem[ip++]
                    dict[wn](this)
                }
            } catch (_: IntEOF) {
                return recorder.output()
            }
        }
    }

    fun see(name: String) {
        _see(vm, vm.dict[name], false)
        print(getOutput())
    }

    fun assertDStack(vararg items: Int) {
        assertContentEquals(items, vm.dstk.asArray())
        vm.dstk.reset()
    }

    fun assertDStackKeep(vararg items: Int) {
        assertContentEquals(items, vm.dstk.asArray())
    }

    fun assertRStack(vararg items: Int) {
        assertContentEquals(items, vm.rstk.asArray())
        vm.dstk.reset()
    }

    fun assertRStackKeep(vararg items: Int) {
        assertContentEquals(items, vm.rstk.asArray())
    }

//    fun assertLStack(vararg items: Int) {
//        assertContentEquals(items, vm.lstk.asArray())
//        vm.dstk.reset()
//    }
//
//    fun assertLStackKeep(vararg items: Int) {
//        assertContentEquals(items, vm.lstk.asArray())
//    }

    fun assertPrinted(s: String) {
        assertEquals(s, getOutput())
    }

    fun getOutput(): String {
        return recorder.output().also {
            recorder.clearOutput()
        }
    }
}