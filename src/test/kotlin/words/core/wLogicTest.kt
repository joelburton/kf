package words.core

import ForthTestCase
import kf.ForthVM.Companion.FALSE
import kf.ForthVM.Companion.TRUE
import kf.words.core.wLogic
import org.junit.jupiter.api.Test

class wLogicTest : ForthTestCase() {
    val mod = wLogic

    @Test
    fun w_zeroLess() {
        vm.dstk.push(0)
        mod.w_zeroLess(vm)
        assertDStack(FALSE)

        vm.dstk.push(-1)
        mod.w_zeroLess(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_zeroEquals() {
        vm.dstk.push(0)
        mod.w_zeroEquals(vm)
        assertDStack(TRUE)

        vm.dstk.push(-1)
        mod.w_zeroEquals(vm)
        assertDStack(FALSE)

        vm.dstk.push(42)
        mod.w_zeroEquals(vm)
        assertDStack(FALSE)
    }

    @Test
    fun w_lessThan() {
        vm.dstk.push(10, 20)
        mod.w_lessThan(vm)
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        mod.w_lessThan(vm)
        assertDStack(FALSE)

        vm.dstk.push(20, 10)
        mod.w_lessThan(vm)
        assertDStack(FALSE)

    }

    @Test
    fun w_equals() {
        vm.dstk.push(10, 20)
        mod.w_equals(vm)
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_equals(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_greaterThan() {
        vm.dstk.push(10, 20)
        mod.w_greaterThan(vm)
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_greaterThan(vm)
        assertDStack(FALSE)

        vm.dstk.push(20, 10)
        mod.w_greaterThan(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_and() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_and(vm)
        assertDStack(0b0100)
    }

    @Test
    fun w_max() {
        vm.dstk.push(10, 20)
        mod.w_max(vm)
        assertDStack(20)

        vm.dstk.push(20, 10)
        mod.w_max(vm)
        assertDStack(20)
    }

    @Test
    fun w_min() {
        vm.dstk.push(10, 20)
        mod.w_min(vm)
        assertDStack(10)

        vm.dstk.push(20, 10)
        mod.w_min(vm)
        assertDStack(10)
    }

    @Test
    fun w_or() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_or(vm)
        assertDStack(0b0101)
    }

    @Test
    fun w_xor() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_xor(vm)
        assertDStack(0b0001)
    }

    @Test
    fun w_invert() {
        vm.dstk.push(0b0101)
        mod.w_invert(vm)
        assertDStack(0b1111111111111111111111111111010)
    }
}