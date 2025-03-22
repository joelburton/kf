package words.core

import EvalForthTestCase
import ForthTestCase
import kf.ForthVM.Companion.REG_STATE
import kf.interps.InterpBase.Companion.STATE_COMPILING
import kf.interps.InterpBase.Companion.STATE_INTERPRETING
import kf.words.core.wIfThen
import kf.words.core.wInterp
import kf.words.custom.wToolsCustom.w_dotCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class wInterpTest : ForthTestCase() {
    val mod = wInterp

    init {
        vm.dict.addModule(wIfThen)
        vm.dict.addModule(wInterp)
    }

    @Test
    fun w_quit() {
        vm.ip = 42
        vm.dstk.push(10)
        vm.rstk.push(20)
        mod.w_quit(vm)
        assertEquals(vm.memConfig.codeStart, vm.ip)
        assertDStack(10)
        assertRStack()
    }

    @Test
    fun w_abort() {
        vm.ip = 42
        vm.dstk.push(10)
        vm.rstk.push(20)
        mod.w_abort(vm)
        assertEquals(vm.memConfig.codeStart, vm.ip)
        assertDStack()
        assertRStack()
        assertPrinted("ABORT\n")
    }

    @Test
    fun w_abortQuoteInterp() {
        vm.ip = 42
        vm.dstk.push(10)
        vm.rstk.push(20)
        vm.scanner.fill("oh no\"")
        mod.w_abortQuote(vm)
        assertEquals(vm.memConfig.codeStart, vm.ip)
        assertDStack()
        assertRStack()
        assertPrinted("ABORT: oh no\n")
    }

    @Test
    fun w_abortQuoteCompile() {
        vm.cstart = 0x100
        vm.cend = 0x100
        vm.ip = vm.memConfig.codeStart
        vm.dstk.push(10)
        vm.rstk.push(20)
        vm.scanner.fill("oh no\"")
        println("${vm.cstart}, ${vm.cend}")
        vm.interp.state = STATE_COMPILING
        mod.w_abortQuote(vm)
        vm.interp.state = STATE_INTERPRETING
        println("${vm.cstart}, ${vm.cend}")
        w_dotCode(vm)
        println(getOutput())
        assertEquals(vm.memConfig.codeStart, vm.ip)
        assertDStack(10)
        assertRStack(20)
    }

    @Test
    fun w_evaluate() {
        TODO()
    }

    @Test
    fun w_state() {
        mod.w_state(vm)
        assertDStack(REG_STATE)
    }

    @Test
    fun w_leftBracket() {
        vm.interp.state = 10
        mod.w_leftBracket(vm)
        assertTrue(vm.interp.isInterpreting)
    }

    @Test
    fun w_rightBracket() {
        vm.interp.state = 10
        mod.w_rightBracket(vm)
        assertTrue(vm.interp.isCompiling)
    }
}

class wInterpFuncTest : EvalForthTestCase() {
    @Test
    fun quit() {
        eval("10 quit 20")
        assertDStack(10)
    }

    @Test
    fun abortStr() {
        vm.dstk.push(10)
        eval("abort\" oh no\"")
        assertDStack()
        assertPrinted("ABORT: oh no\n")
    }

    @Test
    fun abortStrCompile() {
        eval(": test 10 abort\" oh no\" 20 ; test")
        assertDStack()
        assertPrinted("ABORT: oh no\n")
    }
}