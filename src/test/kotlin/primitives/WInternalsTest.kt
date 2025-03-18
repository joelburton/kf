//package primitives
//
//import ForthTestCase
//import kf.primitives.WInternals
//import org.junit.jupiter.api.Test
//
//import org.junit.jupiter.api.Assertions.*
//
//class WInternalsTest : ForthTestCase() {
//    val mod: WInternals = vm.modulesLoaded["Internals"]!! as WInternals
//
//    @Test
//    fun w_ipLoad() {
//        mod.w_ipLoad(vm)
//        assertEquals(vm.cstart, vm.ip)
//    }
//
//    @Test
//    fun w_ipStore() {
//        vm.dstk.push(42)
//        mod.w_ipStore(vm)
//        assertEquals(42, vm.ip)
//    }
//}