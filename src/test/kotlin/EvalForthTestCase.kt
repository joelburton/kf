import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import kf.IntEOF
import kf.ForthVM
import kf.TerminalTestInterface
import kf.interps.InterpBase
import kf.interps.InterpEval
import kf.interps.InterpFast
import kf.words.custom.wToolsCustom._see
import kf.recorder
import kf.words.core.ext.wCompileExt
import kf.words.core.ext.wInterpExt
import kf.words.core.ext.wParseExt
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun dummyFn(vm: ForthVM) {
    vm.ip = 2
}

/** Test cases that need a VM, but doesn't need to eval or load modules. */

open class ForthTestCase {
    val testIO = TerminalTestInterface()
    val vm = ForthVM(
        io = Terminal(
            ansiLevel = AnsiLevel.NONE,
            terminalInterface = testIO
        )
    )

    init {
        vm.interp = InterpBase(vm)
        vm.verbosity = -2
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


    fun assertPrinted(s: String) {
        assertEquals(s, getOutput())
    }

    fun getOutput(): String {
        return recorder.output().also {
            recorder.clearOutput()
        }
    }
}

/** Test case that relies on a full Forth VM w/eval and other mods. */

open class EvalForthTestCase : ForthTestCase() {

    init {
        vm.interp = InterpFast(vm)
        vm.verbosity = -2
    }

    fun eval(s: String): String {
        val io = vm.io.terminalInterface as TerminalTestInterface
        io.addInputs(s)
        with(vm) {
            ip = memConfig.codeStart
            try {
                while (true) {
                    val wn = mem[ip++]
                    val w = dict[wn]
                    w(this)
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
}