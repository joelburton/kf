package words.core

import ForthTestCase
import kf.ForthVM.Companion.REG_BASE
import kf.words.core.wMemory
import kf.words.core.wNumIO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wNumIOTest : ForthTestCase() {
    val mod = wNumIO

    @Test
    fun w_decimal() {
        vm.base = 16
        mod.w_decimal(vm)
        assertEquals(10, vm.base)
    }

    @Test
    fun w_base() {
        mod.w_base(vm)
        assertDStack(REG_BASE)

        vm.dstk.push(2)
        mod.w_base(vm)
        wMemory.w_store(vm)
        assertEquals(2, vm.base)
    }

    @Test
    fun w_dot() {
        vm.base = 10
        vm.dstk.push(10)
        mod.w_dot(vm)
        assertPrinted("10 ")

        vm.base = 16
        vm.dstk.push(10)
        mod.w_dot(vm)
        assertPrinted("a ")
    }
}