package words.core

import ForthTestCase
import kf.dict.Word
import kf.words.core.wCompiling
import kf.words.core.wFunctions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wFunctionsTest : ForthTestCase() {
    val mod = wFunctions

    init {
        vm.dict.addModule(wFunctions)
        vm.dict.addModule(wCompiling) // so that "see" can work
    }

    @Test
    fun w_execute() {
        val w = Word("test", mod::w_call, cpos = 42)
        vm.dict.add(w)
        vm.dstk.push(w.wn)
        mod.w_execute(vm)
        // it doesn't actually run our function, since this test doesn't eval
        // or have a vm loop -- but it sets IP to start of the test function.
        assertEquals(42, vm.ip)
    }

    @Test
    fun w_exit() {
        vm.rstk.push(42)
        mod.w_exit(vm)
        assertRStack()
        assertEquals(42, vm.ip)
    }

    @Test
    fun w_call() {
        val w = Word("test", mod::w_call, cpos = 42)
        vm.currentWord = w
        mod.w_call(vm)
        // it doesn't actually run our function, since this test doesn't eval
        // or have a vm loop -- but it sets IP to  start of the test function.
        assertEquals(42, vm.ip)
    }

}