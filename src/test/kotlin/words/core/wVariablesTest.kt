package words.core

import EvalForthTestCase
import ForthTestCase
import kf.words.core.wVariables
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wVariablesTest : ForthTestCase() {
    val mod = wVariables

    @Test
    fun w_constant() {
        vm.dstk.push(42)
        vm.scanner.fill("life")
        mod.w_constant(vm)
        assertNotNull(vm.dict["life"])

        vm.currentWord = vm.dict["life"]
        vm.dict["life"].fn(vm)
        assertDStack(42)
    }

    @Test
    fun w_variable() {
        vm.scanner.fill("age")
        mod.w_variable(vm)
        assertNotNull(vm.dict["age"])
    }
}

class wVariablesFuncTest : EvalForthTestCase() {
    @Test
    fun constant() {
        eval("42 constant life life")
        assertDStack(42)
    }

    @Test
    fun compiledConstant() {
        eval("42 constant life")
        eval(": a life ;")
        see("a")
        eval("a")
        assertDStack(42)
    }

    @Test
    fun variable() {
        eval("variable age 21 age ! age @")
        assertDStack(21)
    }
}