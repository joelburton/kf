import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import kf.EvalInputSource
import kf.FakeStdInInputSource
import kf.ForthError
import kf.IntEOF
import kf.ForthVM
import kf.InputSource
import kf.StdInInputSource
import kf.TerminalTestInterface
import kf.interps.InterpBase
import kf.interps.InterpFast
import kf.words.custom.wToolsCustom._see
import kf.recorder
import kf.words.machine.wMachine
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun dummyFn(vm: ForthVM) {
    vm.ip = 2
}

/** Test cases that need a VM, but doesn't need to eval or load modules. */

open class ForthTestCase() {
    val testIO = TerminalTestInterface()
    val vm = ForthVM(
        io = Terminal(
            ansiLevel = AnsiLevel.NONE,
            terminalInterface = testIO
        )
    )

    init {
        vm.interp = InterpFast(vm)
        vm.verbosity = -2
        vm.sources.add(FakeStdInInputSource(vm, ""))
    }

    @BeforeEach
    fun beforeEach() {
        recorder.clearOutput()
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

    fun see(name: String) {
        _see(vm, vm.dict[name], false)
        print(getOutput())
    }

    fun setInput(s: String) {
//        (vm.io.terminalInterface as TerminalTestInterface).addInputs(s)
        (vm.source as FakeStdInInputSource).content += s
    }

    fun dump(start: Int, len: Int) {
        for (i in start until start + len) {
            print("${vm.mem[i]} ")
        }
        println()
    }
}


/** Test case that relies on a full Forth VM w/eval and other mods. */

open class EvalForthTestCase : ForthTestCase() {

    @BeforeEach
    fun beforeEachEval() {
        vm.reboot(includePrimitives = true)
    }

    fun eval(s: String): String {
        with(vm) {
            vm.sources.clear()
            vm.sources.add(FakeStdInInputSource(vm, s))
            ip = cstart
            try {
                while (true) {
                    val wn = mem[ip++]
                    val w = dict[wn]
                    w(this)
                }
            } catch (_: IntEOF) {
                return recorder.output()
            } catch (e: ForthError) {
                source.scanner.nextLine()
                rstk.reset()
                ip = cstart
                interp.reset()
                throw e
            }
        }
    }
}