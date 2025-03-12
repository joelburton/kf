import kf.ForthBrk
import kf.ForthBye
import kf.ForthColdStop
import kf.ForthQuit
import kf.WMachine
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class WMachineTest : ForthTestCase() {
    val mod: WMachine
    init {
        mod = vm.modulesLoaded["Machine"]!! as WMachine
    }

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun w_brk() {
        vm.dstk.push(1)
        assertFailsWith<ForthBrk> { mod.w_brk() }
        // doesn't clear stack or reset -- just throws error
        assertDStack(1)
    }

    @Test
    fun w_nop() {
        vm.dstk.push(1)
        mod.w_nop()
        assertDStack(1)
    }

    @Test
    fun w_0branch() {
        TODO()
    }

    @Test
    fun w_branch() {
        eval(": test 3 dup . 1- 0= if return then branch 10 ;")
        see("test")
        TODO()
    }

    @Test
    fun w_0relBranch() {
        TODO()
    }

    @Test
    fun w_relBranch() {
        TODO()
    }

    @Test
    fun w_abort() {
        vm.dstk.push(1)
        mod.w_abort()
        assertDStack()
    }

    @Test
    fun w_reboot() {
        vm.dstk.push(1)
        eval(": a ;")
        val numWords = vm.dict.size
        mod.w_reboot()
        assertDStack()
        assertEquals(numWords - 1, vm.dict.size)
        assertNotNull(vm.dict.getSafe("dup"))
        assertDStack()
    }

    @Test
    fun w_rebootRaw() {
        vm.dstk.push(1)
        mod.w_rebootRaw()
        assertDStack()
        assertNull(vm.dict.getSafe("dup"))
        assertDStack()
    }

    @Test
    fun w_bye() {
        vm.dstk.push(1)
        assertFailsWith<ForthBye> { mod.w_bye()  }
        // doesn't reset -- exits interpreter
        assertDStack(1)
    }

    @Test
    fun w_cold() {
        vm.dstk.push(1)
        assertFailsWith<ForthColdStop> { mod.w_cold()  }
        // cold doesn't reboot -- if not caught, it entirely quits
        assertDStack(1)
    }

    @Test
    fun w_quit() {
        vm.dstk.push(1)
        assertFailsWith<ForthQuit> { mod.w_quit()  }
        // unless interactive, quit doesn't reset -- it throws error
        assertDStack(1)
    }

    @Test
    fun w_lit() {
        vm.mem[vm.cend] = 0x41
        vm.ip = vm.cend
        mod.w_lit()
        assertDStack(0x41)
    }
}