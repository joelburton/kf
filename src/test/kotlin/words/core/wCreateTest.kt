package words.core

import EvalForthTestCase
import ForthTestCase
import kf.Word
import kf.words.core.wCompiling
import kf.words.core.wCreate
import kf.words.core.wFunctions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class wCreateTest : ForthTestCase() {
    val mod = wCreate

    init {
        vm.dict.addModule(wCreate)
        vm.dict.addModule(wFunctions)
        vm.dict.addModule(wCompiling)
        vm.dict.addModule(kf.words.custom.wCreateCustom)
    }

    @Test
    fun w_create() {
        val dend = vm.dend
        vm.scanner.fill("foo")
        mod.w_create(vm)
        val foo = vm.dict["foo"]
        assertEquals(foo.dpos, dend)
        assertEquals(dend, vm.dend)  // shouldn't change
        assertEquals(foo.fn, vm.dict["addr"].fn)
    }

    @Test
    fun w_doesAngle() {
        mod.w_doesAngle(vm)
        assertEquals(vm.dict["(does)"].wn, vm.mem[vm.cend - 2])
        assertEquals(vm.dict["exit"].wn, vm.mem[vm.cend - 1])
    }

    @Test
    fun w_parenDoes() {
        val w = Word("foo", vm.dict["addrcall"].fn)
        vm.dict.add(w)
        vm.currentWord = w
        vm.ip = 0x100
        mod.w_parenDoes(vm)
        assertEquals(vm.dict["addrcall"].fn, w.fn)
        assertEquals(0x101, w.cpos)
    }
}

class wCreateFuncTest : EvalForthTestCase() {
    val mod = wCreate

    @Test
    fun create() {
        eval("create foo")
        assertEquals(vm.dend, vm.dict["foo"].dpos)
    }

    @Test
    fun constant() {
        eval(": const create , does> @ ; 42 const life life")
        assertDStack(42)
    }

    @Test
    fun variable() {
        eval(": var create , ; 42 var life life @")
        assertDStack(42)
    }
}