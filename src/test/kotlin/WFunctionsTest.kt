//import kf.primitives.WFunctions
//import kf.Word
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//
//class WFunctionsTest: ForthTestCase() {
//    val mod: WFunctions = vm.modulesLoaded["Functions"]!! as WFunctions
//    var w: Word
//
//    init {
//        eval(": test 42 ;")
//        w = vm.dict["test"]
//    }
//
//    @Test
//    fun w_call() {
//        vm.currentWord = w
//        mod.w_call(vm)
//        assertEquals(w.cpos, vm.ip)
//        vm.reset()
//
//        // more integration-test-like
//        eval("test")
//        assertDStack(42)
//    }
//
//    @Test
//    fun w_callByAddr() {
//        vm.dstk.push(w.cpos)
//        mod.w_callByAddr(vm)
//        assertEquals(w.cpos, vm.ip)
//        vm.reset()
//
//        // more integration-test-like
//        eval("${w.cpos} call-by-addr")
//        assertDStack(42)
//    }
//
//    @Test
//    fun w_execute() {
//        vm.ip = 0x5000
//        vm.dstk.push(w.wn)
//        mod.w_callByAddr(vm)
//        assertEquals(w.wn, vm.ip)
//        assertEquals(0x5000, vm.rstk.pop())
//        vm.reset()
//
//        // more integration-test-like
//        eval("${w.wn} execute")
//        assertDStack(42)
//    }
//
//    @Test
//    fun w_exit() {
//        vm.rstk.push(0xffff)
//        mod.w_exit(vm)
//        assertEquals(0xffff, vm.ip)
//        assertEquals(0, vm.rstk.size)
//    }
//}