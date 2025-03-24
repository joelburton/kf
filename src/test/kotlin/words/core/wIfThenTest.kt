package words.core

import EvalForthTestCase
import ForthTestCase
import kf.words.core.wIfThen
import kf.words.machine.wMachine
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wIfThenTest : ForthTestCase() {
    val mod = wIfThen

    init {
        vm.dict.addModule(wMachine)
        vm.dict.addModule(wIfThen)
    }

    @Test
    fun w_if() {
        vm.cend = 42
        mod.w_if(vm)
        assertDStack(43)
        assertEquals(vm.dict["0branch"].wn, vm.mem[vm.cend - 2])
        assertEquals(0xffff, vm.mem[vm.cend - 1])
    }

    @Test
    fun w_else() {
        vm.cend = 42
        mod.w_if(vm)
        mod.w_else(vm)
        assertDStack(45)
        assertEquals(vm.dict["branch"].wn, vm.mem[vm.cend - 2])
        assertEquals(0xfffe, vm.mem[vm.cend - 1])
        // check that this fixed the "IF" fwd ref
        assertEquals(3, vm.mem[vm.cend - 3])
    }

    @Test
    fun w_then() {
        // with else
        vm.cend = 42
        mod.w_if(vm)
        mod.w_else(vm)
        mod.w_then(vm)
        // check that this fixed the "ELSE" fwd ref
        assertEquals(0, vm.mem[vm.cend])

        // without else
        vm.cend = 42
        mod.w_if(vm)
        mod.w_then(vm)
        // check that this fixed the "ELSE" fwd ref
        assertEquals(1, vm.mem[vm.cend - 1])
    }
}

class IfThenFuncTest : EvalForthTestCase() {
    @Test
    fun ifThenElse() {
        eval(": test if 20 else 30 then 40 ;")
        see("test")
        println("DONE FIRST")

        eval("10 test")
        assertDStack(20, 40)

        eval("0 test")
        assertDStack(30, 40)
    }
}