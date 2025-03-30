import kf.ForthError
import kf.IntEOF
import kf.ForthVM
import kf.consoles.RecordingForthConsole
import kf.interps.InterpFast
import kf.sources.SourceFakeInteractive
import kf.sources.SourceTestNoInput
import kf.words.custom.wToolsCustom._see
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun dummyFn(vm: ForthVM) {
    vm.ip = 2
}

/** Test cases that need a VM, but doesn't need to eval or load modules. */

open class ForthTestCase() {
    val testIO = RecordingForthConsole()
    val vm = ForthVM(
        io = testIO,
        interp = InterpFast(),
        initVerbosity = -2,
    )

    init {
        // Need to do this here, since the machine is never booted, so it
        // won't have any source (and many tests need a scanner, which comes
        // from the source).
        vm.sources.add(SourceTestNoInput(vm))
    }

    @BeforeEach
    fun beforeEach() {
        testIO.clear()
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
        return testIO.output.also { testIO.clear() }
    }

    fun see(name: String) {
        _see(vm, vm.dict[name], false)
        print(getOutput())
    }

    fun setInput(s: String) {
        (vm.source as SourceFakeInteractive).content += s
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
    fun beforeEachEvalTestFn() {
        vm.reboot(includePrimitives = true)
    }

    fun eval(s: String): String {
        with(vm) {
            vm.sources.clear()
            vm.sources.add(SourceFakeInteractive(vm, s))
            ip = cstart
            try {
                vm.innerRunVM()
            } catch (_: IntEOF) {
                return testIO.output
            } catch (e: ForthError) {
                vm.abort()
                throw e
            }
        }
    }
}