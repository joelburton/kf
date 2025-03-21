package words.core

import ForthTestCase
import kf.CharLitError
import kf.words.core.wChars
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class wCharsTest : ForthTestCase() {
    val mod = wChars

    @Test
    fun w_cStore() {
        vm.dstk.push(42, 0x100)
        mod.w_cStore(vm)
        assertDStack()
        assertEquals(vm.mem[0x100], 42)
    }

    @Test
    fun w_cComma() {
        vm.dend = 0x100
        vm.dstk.push(42)
        mod.w_cComma(vm)
        assertDStack()
        assertEquals(42, vm.mem[0x100])
        assertEquals(0x101, vm.dend)
    }

    @Test
    fun w_cFetch() {
        vm.mem[0x100] = 42
        vm.dstk.push(0x100)
        mod.w_cFetch(vm)
        assertDStack(42)
    }

    @Test
    fun w_char() {
        vm.interp.scanner.fill("AB")
        mod.w_char(vm)
        assertDStack(65)

        // B should be consumed as part of this
        val (_, len) = vm.interp.scanner.parseName()
        assertEquals(0, len)

        vm.interp.scanner.fill("")
        assertFailsWith<CharLitError> { mod.w_char(vm) }
    }

    @Test
    fun w_bracketChar() {
        vm.interp.scanner.fill("AB")
        mod.w_bracketChar(vm)
        assertDStack()
        assertEquals(65, vm.mem[vm.cend - 1])

        // B should be consumed as part of this
        val (_, len) = vm.interp.scanner.parseName()
        assertEquals(0, len)

        vm.interp.scanner.fill("")
        assertFailsWith<CharLitError> { mod.w_char(vm) }
    }

    @Test
    fun w_charPlus() {
        vm.dstk.push(0x100)
        mod.w_charPlus(vm)
        assertDStack(0x101)
    }

    @Test
    fun w_chars() {
        vm.dstk.push(3)
        mod.w_chars(vm)
        assertDStack(3)
    }
}