package words.core.ext

import ForthTestCase
import kf.words.core.ext.wLogicExt
import org.junit.jupiter.api.Test

class wLogicExtTest : ForthTestCase() {
    val mod = wLogicExt

    @Test
    fun w_notEquals() {
        vm.dstk.push(1, 2)
        mod.w_notEquals(vm)
        assertDStack(-1)

        vm.dstk.push(1, 1)
        mod.w_notEquals(vm)
        assertDStack(0)
    }

    @Test
    fun w_true() {
        mod.w_true(vm)
        assertDStack(-1)
    }

    @Test
    fun w_false() {
        mod.w_false(vm)
        assertDStack(0)
    }

    @Test
    fun w_zeroGreater() {
        vm.dstk.push(0)
        mod.w_zeroGreater(vm)
        assertDStack(0)

        vm.dstk.push(10)
        mod.w_zeroGreater(vm)
        assertDStack(-1)
    }

    @Test
    fun w_zeroNotEquals() {
        vm.dstk.push(0)
        mod.w_zeroGreater(vm)
        assertDStack(0)

        vm.dstk.push(10)
        mod.w_zeroGreater(vm)
        assertDStack(-1)
    }

    @Test
    fun w_within() {
        vm.dstk.push(10, 5, 20)
        mod.w_within(vm)
        assertDStack(-1)

        vm.dstk.push(5, 5, 20)
        mod.w_within(vm)
        assertDStack(-1)

        vm.dstk.push(20, 5, 20)
        mod.w_within(vm)
        assertDStack(0)

        vm.dstk.push(1, 5, 20)
        mod.w_within(vm)
        assertDStack(0)

        vm.dstk.push(25, 5, 20)
        mod.w_within(vm)
        assertDStack(0)
    }

}