//package primitives
//
//import ForthTestCase
//import kf.primitives.WStackOps
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//
//class WStackOpsTest : ForthTestCase() {
//    @Test
//    fun w_rot() {
//        vm.dstk.push(10)
//        vm.dstk.push(20)
//        vm.dstk.push(30)
//        WStackOps.w_rot(vm)
//        assertDStack(20, 30, 10)
//    }
//
//    @Test
//    fun w_dup() {
//        vm.dstk.push(10)
//        WStackOps.w_dup(vm)
//        assertDStack(10, 10)
//    }
//
//    @Test
//    fun w_drop() {
//        vm.dstk.push(10)
//        WStackOps.w_drop(vm)
//        assertDStack()
//    }
//
//    @Test
//    fun w_swap() {
//        vm.dstk.push(10)
//        vm.dstk.push(20)
//        WStackOps.w_swap(vm)
//        assertDStack(20, 10)
//    }
//
//    @Test
//    fun w_toR() {
//        vm.dstk.push(10)
//        WStackOps.w_toR(vm)
//        assertDStack()
//        assertRStack(10)
//    }
//
//    @Test
//    fun w_rFrom() {
//        vm.rstk.push(10)
//        WStackOps.w_rFrom(vm)
//        assertDStack(10)
//        assertRStack()
//    }
//
////    @Test
////    fun w_toL() {
////        vm.dstk.push(10)
////        WStackOps.w_toL(vm)
////        assertDStack()
////        assertLStack(10)
////    }
////
////    @Test
////    fun w_lFrom() {
////        vm.lstk.push(10)
////        WStackOps.w_lFrom(vm)
////        assertDStack(10)
////        assertLStack()
////    }
//
//    @Test
//    fun w_nip() {
//        vm.dstk.push(10)
//        vm.dstk.push(20)
//        WStackOps.w_nip(vm)
//        assertDStack(20)
//    }
//
//    @Test
//    fun w_over() {
//        vm.dstk.push(10)
//        vm.dstk.push(20)
//        WStackOps.w_over(vm)
//        assertDStack(10, 20, 10)
//    }
//
//    @Test
//    fun w_spFetch() {
//        WStackOps.w_spFetch(vm)
//        val init = vm.dstk.pop()
//        vm.dstk.push(10)
//        WStackOps.w_spFetch(vm)
//        val after = vm.dstk.pop()
//        assertEquals(-1, after-init)
//    }
//
//    @Test
//    fun w_spStore() {
//        vm.dstk.push(0x350)
//        WStackOps.w_spStore(vm)
//        assertEquals(0x350, vm.dstk.sp)
//    }
//
//    @Test
//    fun w_rpFetch() {
//        WStackOps.w_rpFetch(vm)
//        val init = vm.dstk.pop()
//        vm.rstk.push(10)
//        WStackOps.w_rpFetch(vm)
//        val after = vm.dstk.pop()
//        assertEquals(-1, after- init)
//    }
//
//    @Test
//    fun w_rpStore() {
//        vm.dstk.push(0x3e5)
//        WStackOps.w_rpStore(vm)
//        assertEquals(0x3e5, vm.rstk.sp)
//    }
//
////    @Test
////    fun w_lpFetch() {
////        WStackOps.w_lpFetch(vm)
////        val init = vm.dstk.pop()
////        vm.lstk.push(10)
////        WStackOps.w_lpFetch(vm)
////        val after = vm.dstk.pop()
////        assertEquals(-1,  after-init)
////    }
////
////    @Test
////    fun w_lpStore() {
////        vm.dstk.push(0x3f5)
////        WStackOps.w_lpStore(vm)
////        assertEquals(0x3f5, vm.lstk.sp)
////    }
//
//    @Test
//    fun w_depth() {
//        vm.dstk.push(10)
//        vm.dstk.push(20)
//        WStackOps.w_depth(vm)
//        assertDStack(10, 20, 2)
//    }
//
//    @Test
//    fun w_dotS() {
//        vm.dstk.push(10)
//        vm.dstk.push(20)
//        WStackOps.w_dotS(vm)
//        assertDStack(10, 20)
//        assertPrinted("<2> 10 20")
//    }
//
//}