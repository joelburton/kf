package primitives

import EvalForthTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WStackOpsTest : EvalForthTestCase() {
    val mod = kf.words.core.wStackOps
    val ext = kf.words.core.ext.wStackOpsExt
    val rstack = kf.words.core.wRStack
    val custom = kf.words.custom.wStacksCustom

    @Test
    fun w_rot() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        vm.dstk.push(30)
        mod.w_rot(vm)
        assertDStack(20, 30, 10)
    }

    @Test
    fun w_dup() {
        vm.dstk.push(10)
        mod.w_dup(vm)
        assertDStack(10, 10)
    }

    @Test
    fun w_drop() {
        vm.dstk.push(10)
        mod.w_drop(vm)
        assertDStack()
    }

    @Test
    fun w_swap() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        mod.w_swap(vm)
        assertDStack(20, 10)
    }

    @Test
    fun w_toR() {
        vm.dstk.push(10)
        rstack.w_toR(vm)
        assertDStack()
        assertRStack(10)
    }

    @Test
    fun w_rFrom() {
        vm.rstk.push(10)
        rstack.w_rFrom(vm)
        assertDStack(10)
        assertRStack()
    }

    @Test
    fun w_nip() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        ext.w_nip(vm)
        assertDStack(20)
    }

    @Test
    fun w_over() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        mod.w_over(vm)
        assertDStack(10, 20, 10)
    }

    @Test
    fun w_spFetch() {
        custom.w_spFetch(vm)
        val init = vm.dstk.pop()
        vm.dstk.push(10)
        custom.w_spFetch(vm)
        val after = vm.dstk.pop()
        assertEquals(-1, after-init)
    }

    @Test
    fun w_spStore() {
        vm.dstk.push(0x350)
        custom.w_spStore(vm)
        assertEquals(0x350, vm.dstk.sp)
    }

    @Test
    fun w_rpFetch() {
        custom.w_rpFetch(vm)
        val init = vm.dstk.pop()
        vm.rstk.push(10)
        custom.w_rpFetch(vm)
        val after = vm.dstk.pop()
        assertEquals(-1, after- init)
    }

    @Test
    fun w_rpStore() {
        vm.dstk.push(0x3e5)
        custom.w_rpStore(vm)
        assertEquals(0x3e5, vm.rstk.sp)
    }

    @Test
    fun w_depth() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        mod.w_depth(vm)
        assertDStack(10, 20, 2)
    }

    @Test
    fun w_dotS() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        kf.words.tools.wTools.w_dotS(vm)
        assertDStack(10, 20)
        assertPrinted("<2> 10 20")
    }

}