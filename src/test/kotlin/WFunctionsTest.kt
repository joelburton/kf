import kf.WFunctions
import kf.Word
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WFunctionsTest: ForthTestCase() {
    val mod: WFunctions
    var w: Word

    init {
        mod = vm.modulesLoaded["Functions"]!! as WFunctions
        eval(": test 42 ;")
        w = vm.dict["test"]
    }

    @Test
    fun w_call() {
        vm.currentWord = w
        mod.w_call()
        assertEquals(w.cpos, vm.ip)
        vm.reset()

        // more integration-test-like
        eval("test")
        assertDStack(42)
    }

    @Test
    fun w_callByAddr() {
        vm.dstk.push(w.cpos)
        mod.w_callByAddr()
        assertEquals(w.cpos, vm.ip)
        vm.reset()

        // more integration-test-like
        eval("${w.cpos} call-by-addr")
        assertDStack(42)
    }

    @Test
    fun w_execute() {
        vm.ip = 0x5000
        vm.dstk.push(w.wn)
        mod.w_callByAddr()
        assertEquals(w.wn, vm.ip)
        assertEquals(0x5000, vm.rstk.pop())
        vm.reset()

        // more integration-test-like
        eval("${w.wn} execute")
        assertDStack(42)
    }

    @Test
    fun w_return() {
        vm.rstk.push(0xffff)
        mod.w_return()
        assertEquals(0xffff, vm.ip)
        assertEquals(0, vm.rstk.size)
    }
}