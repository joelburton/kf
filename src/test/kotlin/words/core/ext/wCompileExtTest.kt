package words.core.ext

import EvalForthTestCase
import ForthTestCase
import kf.interps.InterpBase
import kf.interps.InterpForth
import kf.words.core.ext.wCompileExt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class wCompileExtTest : ForthTestCase() {
    val mod = wCompileExt

    init {
        vm.interp = InterpForth(vm)
        vm.dict.addModule(mod)
    }

    @Test
    fun w_bracketCompile() {
        // compiling a non-immediate word
        vm.interp.state = InterpBase.Companion.STATE_COMPILING
        var wn = vm.dict["compile,"].wn
        vm.ip = 0x200
        vm.mem[0x200] = wn
        vm.cend = 0x100
        mod.w_bracketCompile(vm)
        assertEquals(wn, vm.mem[0x100])
        vm.mem[0x100] = 0x00

        // compiling an immediate word
        wn = vm.dict[".("].wn
        vm.ip = 0x200
        vm.mem[0x200] = wn
        vm.cend = 0x100
        vm.scanner.fill("msg)")
        mod.w_bracketCompile(vm)
        assertEquals(0x00, vm.mem[0x100])
        assertPrinted("msg")
    }

    @Test
    fun w_compileComma() {
        // compiling a non-immediate word
        vm.interp.state = InterpBase.Companion.STATE_COMPILING
        var wn = vm.dict["compile,"].wn
        vm.dstk.push(wn)
        vm.cend = 0x100
        mod.w_compileComma(vm)
        assertEquals(wn, vm.mem[0x100])
        vm.mem[0x100] = 0x00

        // compiling an immediate word
        wn = vm.dict[".("].wn
        vm.dstk.push(wn)
        vm.cend = 0x100
        vm.scanner.fill("msg)")
        mod.w_compileComma(vm)
        assertEquals(0x00, vm.mem[0x100])
        assertPrinted("msg")
    }

    @Test
    fun w_dotParen() {
        vm.scanner.fill("msg)")
        mod.w_dotParen(vm)
        assertPrinted("msg")
    }

    @Test
    fun w_colonNoName() {
        mod.w_colonNoName(vm)
        assertEquals("(ANON)", vm.dict.last.name.uppercase())
        assertTrue(vm.dict.last.hidden)
    }
}


class wCompileExtFuncTest : EvalForthTestCase() {
    @Test
    fun noName() {
        eval(":NONAME 10 20 ;")
        eval("EXECUTE")
        assertDStack(10, 20)
    }
}