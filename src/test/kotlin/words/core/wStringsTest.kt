package words.core

import EvalForthTestCase
import ForthTestCase
import kf.words.core.wStrings
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wStringsTest : ForthTestCase() {
    val mod = wStrings

    @Test
    fun w_count() {
        vm.dend = 0x200
        vm.appendStrToData("ABC")
        vm.dstk.push(0x200)
        mod.w_count(vm)
        assertDStack(0x201, 3)
    }

    @Test
    fun w_dotQuote() {
        vm.scanner.fill(" hello\"")
        wStrings.w_dotQuote(vm)
        assertPrinted(" hello")
    }

    @Test
    fun w_sQuote() {
        vm.dend = 0x200
        vm.scanner.fill(" hello\"")
        wStrings.w_sQuote(vm)
        assertDStack(0x201, 6)
    }

    @Test
    fun w_type() {
        vm.dend = 0x200
        vm.scanner.fill(" hello\"")
        wStrings.w_sQuote(vm)
        mod.w_type(vm)
        assertPrinted(" hello")
    }
}

class wStringsFuncTest: EvalForthTestCase() {
    @Test
    fun printing() {
        eval(": test .\"  hello\" ; test")
        assertPrinted(" hello")
    }

    @Test
    fun gettingStr() {
        eval(": test s\"  hello\" type ; test")
        assertPrinted(" hello")
    }
}