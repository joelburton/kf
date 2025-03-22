package words.core

import ForthTestCase
import kf.words.core.wStackOps
import org.junit.jupiter.api.Test

class wStackOpsTest : ForthTestCase() {
    val mod = wStackOps

    @Test
    fun w_questionDup() {
    }

    @Test
    fun w_twoDrop() {
        vm.dstk.push(10, 20, 30)
        mod.w_twoDrop(vm)
        assertDStack(10)
    }

    @Test
    fun w_twoDup() {
        vm.dstk.push(5, 10, 20)
        mod.w_twoDup(vm)
        assertDStack(5, 10, 20, 10, 20)
    }

    @Test
    fun w_twoOver() {
        vm.dstk.push(10, 20, 30, 40)
        mod.w_twoOver(vm)
        assertDStack(10, 20, 30, 40, 10, 20)
    }

    @Test
    fun w_twoSwap() {
        vm.dstk.push(10, 20, 30, 40)
        mod.w_twoSwap(vm)
        assertDStack(30, 40, 10, 20)
    }

    @Test
    fun w_drop() {
        vm.dstk.push(10, 20)
        mod.w_drop(vm)
        assertDStack(10)
    }

    @Test
    fun w_dup() {
        vm.dstk.push(20, 10)
        mod.w_dup(vm)
        assertDStack(20, 10, 10)
    }

    @Test
    fun w_rot() {
        vm.dstk.push(10, 20, 30)
        mod.w_rot(vm)
        assertDStack(20, 30, 10)
    }

    @Test
    fun w_over() {
        vm.dstk.push(10)
        vm.dstk.push(20)
        mod.w_over(vm)
        assertDStack(10, 20, 10)
    }

    @Test
    fun w_swap() {
        vm.dstk.push(5, 10, 20)
        mod.w_swap(vm)
        assertDStack(5, 20, 10)
    }

    @Test
    fun w_depth() {
        vm.dstk.push(10, 20)
        mod.w_depth(vm)
        assertDStack(10, 20, 2)
    }
}