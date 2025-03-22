package words.core

import ForthTestCase
import kf.ForthBufferError
import kf.ForthIOError
import kf.ForthVM
import kf.words.core.wIO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class wIOTest : ForthTestCase() {
    val mod = wIO

    @Test
    fun w_cr() {
        mod.w_cr(vm)
        assertPrinted("\n")
    }

    @Test
    fun w_space() {
        mod.w_space(vm)
        assertPrinted(" ")
    }

    @Test
    fun w_spaces() {
        vm.dstk.push(3)
        mod.w_spaces(vm)
        assertPrinted("   ")

        vm.dstk.push(-1)
        mod.w_spaces(vm)
        assertPrinted("")
    }

    @Test
    fun w_emit() {
        vm.dstk.push(65)
        mod.w_emit(vm)
        vm.dstk.push(66)
        mod.w_emit(vm)
        assertPrinted("AB")
    }

    @Test
    fun w_decimal() {
        mod.w_decimal(vm)
        assertEquals(10, vm.base)

    }

    @Test
    fun w_base() {
        mod.w_base(vm)
        assertDStack(ForthVM.REG_BASE)

    }

    @Test
    fun w_key() {
        assertFailsWith<ForthIOError> { mod.w_key(vm) }
    }


    @Test
    fun w_bl() {
        mod.w_bl(vm)
        assertDStack(0x20)
    }

    @Test
    fun w_accept() {
        vm.dstk.push(vm.memConfig.padStart)
        vm.dstk.push(5)
        setInput("ABC")
        mod.w_accept(vm)
        assertDStack(3)
        assertEquals(65, vm.mem[vm.memConfig.padStart] + 0)
        assertEquals(66, vm.mem[vm.memConfig.padStart] + 1)
        assertEquals(67, vm.mem[vm.memConfig.padStart] + 2)

        vm.dstk.push(vm.memConfig.padStart)
        vm.dstk.push(5)
        setInput("ABCDEFGH")
        assertFailsWith<ForthBufferError> {  mod.w_accept(vm) }
    }
}