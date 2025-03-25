package words.core.ext

import EvalForthTestCase
import ForthTestCase
import kf.WordValueAssignError
import kf.words.core.ext.wValuesExt
import kf.words.core.wCreate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class wValuesExtTest : ForthTestCase() {
    val mod = wValuesExt

    init {
        vm.dict.addModule(wCreate)
    }

    @Test
    fun w_value() {
        vm.dstk.push(42)
        vm.source.scanner.fill("life")
        mod.w_value(vm)
        assertNotNull(vm.dict["life"])

        vm.currentWord = vm.dict["life"]
        vm.dict["life"].fn(vm)
        assertDStack(42)
    }
}

class wVariablesFuncTest : EvalForthTestCase() {
    @Test
    fun value() {
        eval("42 value life life")
        assertDStack(42)

        eval("50 to life life")
        assertDStack(50)
    }

    @Test
    fun cantRevalueNonValues() {
        eval("42 constant life life")
        assertDStack(42)

        assertFailsWith<WordValueAssignError> {
            eval("50 to life life")
        }
    }
}

