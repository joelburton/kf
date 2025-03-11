import kf.ForthVM
import kf.IOGateway
import kf.WFunctions
import kf.Word
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class WFunctionsTest {
    val vm = ForthVM(io = IOGateway())
    val funcs = WFunctions(vm)
    var w: Word

    init {
        vm.reboot()
        forthEval(vm, ": test 42 ;")
        w = vm.dict.get("test")
    }

    fun rez(): Int {
        assertEquals(1, vm.dstk.size)
        return vm.dstk.pop()
    }

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun w_call() {
        vm.currentWord = w
        funcs.w_call()
        assertEquals(w.cpos, vm.ip)
        vm.reset()

        // more integration-test-like
        forthEval(vm, "test")
        assertEquals(42, rez())
    }

    @Test
    fun w_callByAddr() {
        vm.dstk.push(w.cpos)
        funcs.w_callByAddr()
        assertEquals(w.cpos, vm.ip)
        vm.reset()

        // more integration-test-like
        forthEval(vm, "${w.cpos} call-by-addr")
        assertEquals(42, rez())
    }

    @Test
    fun w_execute() {
        vm.ip = 0x5000
        vm.dstk.push(w.wn!!)
        funcs.w_callByAddr()
        assertEquals(w.wn!!, vm.ip)
        assertEquals(0x5000, vm.rstk.pop())
        vm.reset()

        // more integration-test-like
        forthEval(vm, "${w.wn} execute")
        assertEquals(42, rez())
    }

    @Test
    fun w_return() {
        vm.rstk.push(0xffff)
        funcs.w_return()
        assertEquals(0xffff, vm.ip)
        assertEquals(0, vm.rstk.size)
    }
}