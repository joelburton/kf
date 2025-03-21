import kf.IntBrk
import kf.IntBye
import kf.IntQuit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class WMachineTest : EvalForthTestCase() {
    val mod = kf.words.machine.wMachine

    @Test
    fun w_brk() {
        vm.dstk.push(1)
        assertFailsWith<IntBrk> { mod.w_brk(vm) }
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
        eval("test")
        assertPrinted("3 2 1 ")
    }

    @Test
    fun w_0branchAbs() {
        vm.dstk.push(0)
        vm.mem[0x100] = 0x10
        vm.ip = 0x100
        mod.w_0branchAbs(vm)
        assertEquals(0x110, vm.ip)
        assertDStack()

        vm.dstk.push(1)
        vm.mem[0x100] = 0x102
        vm.ip = 0x100
        mod.w_0branchAbs(vm)
        assertEquals(0x101, vm.ip)
        assertDStack()
    }

    @Test
    fun w_branchAbs() {
        vm.mem[0x100] = 0x10
        vm.ip = 0x100
        mod.w_branchAbs(vm)
        assertEquals(0x110, vm.ip)
    }

    @Test
    fun w_abort() {
        vm.dstk.push(1)
        kf.words.core.wInterp.w_abort(vm)
        assertDStack()
    }

    @Test
    fun w_cold() {
        vm.dstk.push(1)
        eval(": a ;")
        val numWords = vm.dict.size
        mod.w_cold(vm)
        assertDStack()
        assertEquals(numWords - 1, vm.dict.size)
        assertNotNull(vm.dict.getSafe("dup"))
        assertDStack()
    }

    @Test
    fun w_coldRaw() {
        vm.dstk.push(1)
        mod.w_coldRaw(vm)
        assertDStack()
        assertNull(vm.dict.getSafe("dup"))
        assertDStack()
    }

    @Test
    fun w_bye() {
        vm.dstk.push(1)
        assertFailsWith<IntBye> { kf.words.tools.wToolsExt.w_bye(vm)  }
        // doesn't reset -- exits interpreter
        assertDStack(1)
    }

    @Test
    fun w_quit() {
        vm.dstk.push(1)
        assertFailsWith<IntQuit> { kf.words.core.wInterp.w_quit(vm)  }
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