package words.core

import EvalForthTestCase
import ForthTestCase
import kf.*
import kf.interps.InterpBase.Companion.STATE_INTERPRETING
import kf.words.core.ext.wCompileExt
import kf.words.core.ext.wInterpExt
import kf.words.core.wCompiling
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class wCompilingTest : ForthTestCase() {
    val mod = wCompiling

    init {
        vm.dict.addModule(wCompiling)
        vm.dict.addModule(wCompileExt)
    }

    @Test
    fun w_colon() {
        assertNull(vm.dict.currentlyDefining)
        vm.interp.scanner.fill("test")
        mod.w_colon(vm)
        val w = vm.dict.last
        assertEquals("test", w.name)
        assertTrue(w.hidden)
        assertNotNull(vm.dict.currentlyDefining)
        assertNotNull(vm.dict["test"])
        assertTrue(vm.interp.isCompiling)
    }

    @Test
    fun w_semicolon() {
        vm.interp.state = STATE_INTERPRETING
        val w = Word("test", ::w_notImpl, hidden = true)
        vm.dict.add(w)
        mod.w_semicolon(vm)
        assertEquals(w, vm.dict.last)
        assertNull(vm.dict.currentlyDefining)
        assertTrue(vm.interp.isInterpreting)
    }

    @Test
    fun w_immediate() {
        val w = Word("test", ::w_notImpl, hidden = true)
        vm.dict.add(w)
        mod.w_immediate(vm)
        assertTrue(w.imm)
    }

    @Test
    fun w_recurse() {
        val w = Word("test", ::w_notImpl, hidden = true)
        vm.dict.add(w)
        mod.w_recurse(vm)
        assertEquals(w.wn, vm.mem[vm.cend - 1])
    }

    @Test
    fun w_postpone() {
        val w = Word("test", ::w_notImpl, hidden = true)
        vm.dict.add(w)
        vm.interp.scanner.fill("test")
        mod.w_postpone(vm)
        assertEquals(vm.dict["[COMPILE]"].wn, vm.mem[vm.cend - 2])
        assertEquals(w.wn, vm.mem[vm.cend - 1])
    }

    @Test
    fun w_literal() {
        vm.dstk.push(42)
        mod.w_literal(vm)
        assertEquals(vm.dict["lit"].wn, vm.mem[vm.cend - 2])
        assertEquals(42, vm.mem[vm.cend - 1])
    }
}

class wCompilingFuncTest : EvalForthTestCase() {

    @BeforeEach
    fun beforeEach() {
        vm.reboot(true)
    }

    @Test
    fun canDefineWords() {
        eval(": test 123 ;")
        println(vm.dict.words)
        assertNotNull(vm.dict["test"])
        eval("test")
        assertDStack(123)
        assertFailsWith<InvalidState> { eval(": a : test1 123 ;") }
        assertFailsWith<InvalidState> { eval(": test2 123 ; ;") }
    }

    @Test
    fun immediateMode() {
        // normal func
        eval(": f1 42 ;")
        assertDStack()
        eval(": use1 f1 ;")
        assertDStack()

        // immediate
        eval(": f2 immediate 42 ;")
        assertDStack()
        eval(": use2 f2 42 ;")
        assertDStack(42)

        // immediate
        eval(": f3 42 ; immediate")
        assertDStack()
        eval(": use3 f3 42 ;")
        assertDStack(42)
    }

    @Test
    fun recursionWorks() {
        // normal func
        assertFailsWith<ParseError> { eval(": f1 f1 ;") }

        // normal func w/predecessor
        eval(": f2 42 ;")
        eval(": f2 f2 ;")
        eval("f2")
        assertDStack(42)
        // recursive func

        recorder.clearOutput()
        eval(": f3 1- dup dup . 0 > if recurse then ; 3 f3 ")
        assertPrinted("2 1 0 ")
    }

    @Test
    fun postponeWorks() {
        eval(": plus1 1+ ;")
        eval(": plus2 immediate postpone plus1 postpone plus1 ;")
        assertDStack()

        // You shouldn't do this --- plus2 is meant to be used in other defs;
        // but it does work
        eval("10 plus2")
        assertDStack(12)
        assertTrue(recorder.output().length > 10)
        recorder.clearOutput()

        eval(": uses-plus2 10 plus2 ;")
        assertDStack()

        eval("uses-plus2")
        assertDStack(12)
        assertTrue(recorder.output().isEmpty())

        eval(": iff immediate postpone if ;")
        eval(": test 10 iff 20 then ; test")
        assertDStack(20)
        assertRStack()

        eval(": do_ immediate postpone do_ ;")
        eval(": test 3 0 do_ 10 loop ; test")
        assertDStack(10, 10, 10)
    }
}