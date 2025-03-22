//import kf.ForthVM.Companion.FALSE
//import kf.ForthVM.Companion.TRUE
//import org.junit.jupiter.api.Test
//
//class WLogicTest : EvalForthTestCase() {
//    override val mod = kf.words.core.wLogic
//    val ext = kf.words.core.ext.wLogicExt
//    val custom = kf.words.custom.wLogicCustom
//
//    @Test
//    fun w_not() {
//        vm.dstk.push(0b0101)
//        custom.w_not(vm)
//        assertDStack(0b1111111111111111111111111111010)
//    }
//
//    @Test
//    fun w_gte() {
//        vm.dstk.push(10, 20)
//        custom.w_gte(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(10, 10)
//        custom.w_gte(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(20, 10)
//        custom.w_gte(vm)
//        assertDStack(TRUE)
//    }
//
//    @Test
//    fun w_lte() {
//        vm.dstk.push(10, 20)
//        custom.w_lte(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(10, 10)
//        custom.w_lte(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(20, 10)
//        custom.w_lte(vm)
//        assertDStack(FALSE)
//    }
//
//    @Test
//    fun w_ne() {
//        vm.dstk.push(10, 20)
//        ext.w_ne(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(10, 10)
//        ext.w_ne(vm)
//        assertDStack(FALSE)
//    }
//
//    @Test
//    fun w_true() {
//        ext.w_true(vm)
//        assertDStack(TRUE)
//    }
//
//    @Test
//    fun w_false() {
//        ext.w_false(vm)
//        assertDStack(FALSE)
//    }
//}