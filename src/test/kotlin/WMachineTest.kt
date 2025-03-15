import kf.ForthBrk
import kf.ForthBye
import kf.ForthColdStop
import kf.ForthQuit
import kf.primitives.WMachine
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class WMachineTest : ForthTestCase() {
    val mod: WMachine = vm.modulesLoaded["Machine"]!! as WMachine

    @Test
    fun w_brk() {
        vm.dstk.push(1)
        assertFailsWith<ForthBrk> { mod.w_brk(vm) }
        // doesn't clear stack or reset -- just throws error
        assertDStack(1)
    }

    @Test
    fun w_nop() {
        vm.dstk.push(1)
        mod.w_nop(vm)
        assertDStack(1)
    }

    @Test
    fun w_0branch() {
        vm.dstk.push(0)
        vm.mem[0x100] = 0x102
        vm.ip = 0x100
        mod.w_0branch(vm)
        assertEquals(0x102, vm.ip)
        assertDStack()

        vm.dstk.push(1)
        vm.mem[0x100] = 0x102
        vm.ip = 0x100
        mod.w_0branch(vm)
        assertEquals(0x101, vm.ip)
        assertDStack()
    }

    @Test
    fun w_branch() {
        vm.mem[0x100] = 0x102
        vm.ip = 0x100
        mod.w_branch(vm)
        assertEquals(0x102, vm.ip)
    }

    @Test
    fun w_branchIntegration() {
        vm.cend = 0x100
        eval(": test 3 dup . 1- dup 0= if return then branch [ 0x102 ,, ] ;")
        eval("test")
        assertPrinted("3 2 1 ")
    }

    @Test
    fun w_0branchIntegration() {
        vm.cend = 0x100
        eval(": test 3 dup . 1- dup 0= 0branch [ 0x102 ,, ] ;")
        eval("test")
        assertPrinted("3 2 1 ")
    }

    @Test
    fun w_relBranchIntegration() {
        vm.cend = 0x100
        eval(": test 3 dup . 1- dup 0= if return then rel-branch [ -9 ,, ] ;")
        eval("test")
        assertPrinted("3 2 1 ")
    }

    @Test
    fun w_0relBranchIntegration() {
        vm.cend = 0x100
        eval(": test 3 dup . 1- dup 0= 0rel-branch [ -6 ,, ] ;")
        see("test")
        eval("test")
        assertPrinted("3 2 1 ")
    }

    @Test
    fun w_0relBranch() {
        vm.dstk.push(0)
        vm.mem[0x100] = 0x10
        vm.ip = 0x100
        mod.w_0relBranch(vm)
        assertEquals(0x110, vm.ip)
        assertDStack()

        vm.dstk.push(1)
        vm.mem[0x100] = 0x102
        vm.ip = 0x100
        mod.w_0relBranch(vm)
        assertEquals(0x101, vm.ip)
        assertDStack()
    }

    @Test
    fun w_relBranch() {
        vm.mem[0x100] = 0x10
        vm.ip = 0x100
        mod.w_relBranch(vm)
        assertEquals(0x110, vm.ip)
    }

    @Test
    fun w_abort() {
        vm.dstk.push(1)
        mod.w_abort(vm)
        assertDStack()
    }

    @Test
    fun w_reboot() {
        vm.dstk.push(1)
        eval(": a ;")
        val numWords = vm.dict.size
        mod.w_reboot(vm)
        assertDStack()
        assertEquals(numWords - 1, vm.dict.size)
        assertNotNull(vm.dict.getSafe("dup"))
        assertDStack()
    }

    @Test
    fun w_rebootRaw() {
        vm.dstk.push(1)
        mod.w_rebootRaw(vm)
        assertDStack()
        assertNull(vm.dict.getSafe("dup"))
        assertDStack()
    }

    @Test
    fun w_bye() {
        vm.dstk.push(1)
        assertFailsWith<ForthBye> { mod.w_bye(vm)  }
        // doesn't reset -- exits interpreter
        assertDStack(1)
    }

    @Test
    fun w_cold() {
        vm.dstk.push(1)
        assertFailsWith<ForthColdStop> { mod.w_cold(vm)  }
        // cold doesn't reboot -- if not caught, it entirely quits
        assertDStack(1)
    }

    @Test
    fun w_quit() {
        vm.dstk.push(1)
        assertFailsWith<ForthQuit> { mod.w_quit(vm)  }
        // unless interactive, quit doesn't reset -- it throws error
        assertDStack(1)
    }

    @Test
    fun w_lit() {
        vm.mem[vm.cend] = 0x41
        vm.ip = vm.cend
        mod.w_lit(vm)
        assertDStack(0x41)
    }
}