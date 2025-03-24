package words.core

import EvalForthTestCase
import ForthTestCase
import kf.ForthVM
import kf.words.core.wParsing
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wParsingTest : ForthTestCase() {
    val mod = wParsing

    @Test
    fun w_word() {
        vm.dstk.push('"'.code)
        vm.source.scanner.fill("  AB\"")
        mod.w_word(vm)
        val addr = vm.dstk.pop()
        assertDStack()
        assertEquals(vm.memConfig.scratchStart, addr)
        assertEquals(4, vm.mem[addr])
        assertEquals(32, vm.mem[addr + 1])
        assertEquals(32, vm.mem[addr + 2])
        assertEquals(65, vm.mem[addr + 3])
        assertEquals(66, vm.mem[addr + 4])
    }

    @Test
    fun w_source() {
        vm.source.scanner.fill("ABC")
        mod.w_source(vm)
        assertDStack(vm.source.scanner.start, 3)
    }

    @Test
    fun w_toIn() {
        mod.w_toIn(vm)
        assertDStack(ForthVM.Companion.REG_IN_PTR)
    }

    @Test
    fun w_toNumber() {
        vm.base = 10
        vm.source.scanner.fill("123")
        vm.dstk.dblPush(0) // doesn't really matter; currently ignored
        vm.dstk.push(vm.source.scanner.start, 3)
        mod.w_toNumber(vm)
        assertDStack(123, 0, 0, 0)

        vm.base = 16
        vm.source.scanner.fill("0xFF")
        vm.dstk.dblPush(0) // doesn't really matter; currently ignored
        vm.dstk.push(vm.source.scanner.start, 4)
        mod.w_toNumber(vm)
        assertDStack(255, 0, 0, 0)

        // this will need more tests once the function is more complete ...
    }
}

class wParsingFuncTest : EvalForthTestCase() {

    @Test
    fun quine() {
        // the only test that really matters
        eval("SOURCE TYPE")
        assertPrinted("SOURCE TYPE")
    }
}