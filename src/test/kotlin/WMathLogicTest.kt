//import kf.primitives.WMathLogic
//import org.junit.jupiter.api.Test
//import kf.ForthVM.Companion.TRUE
//import kf.ForthVM.Companion.FALSE
//
//class WMathLogicTest : ForthTestCase() {
//    val mod: WMathLogic = vm.modulesLoaded["MathLogic"]!! as WMathLogic
//
//    @Test
//    fun w_and() {
//        vm.dstk.push(0b0101, 0b0100)
//        mod.w_and(vm)
//
//        assertDStack(0b0100)
//    }
//
//    @Test
//    fun w_or() {
//        vm.dstk.push(0b0101, 0b0100)
//        mod.w_or(vm)
//        assertDStack(0b0101)
//    }
//
//    @Test
//    fun w_not() {
//        vm.dstk.push(0b0101)
//        mod.w_not(vm)
//        assertDStack(0b1111111111111111111111111111010)
//    }
//
//    @Test
//    fun w_xor() {
//        vm.dstk.push(0b0101, 0b0100)
//        mod.w_xor(vm)
//        assertDStack(0b0001)
//    }
//
//    @Test
//    fun w_negate() {
//        vm.dstk.push(5)
//        mod.w_negate(vm)
//        assertDStack(-5)
//
//        vm.dstk.push(-5)
//        mod.w_negate(vm)
//        assertDStack(5)
//    }
//
//    @Test
//    fun w_inv() {
//        vm.dstk.push(0b0101)
//        mod.w_inv(vm)
//        assertDStack(0b1111111111111111111111111111010)
//    }
//
//    @Test
//    fun w_add() {
//        vm.dstk.push(10, 20)
//        mod.w_add(vm)
//        assertDStack(30)
//    }
//
//    @Test
//    fun w_sub() {
//        vm.dstk.push(10, 20)
//        mod.w_sub(vm)
//        assertDStack(-10)
//    }
//
//    @Test
//    fun w_mul() {
//        vm.dstk.push(10, 20)
//        mod.w_mul(vm)
//        assertDStack(200)
//    }
//
//    @Test
//    fun w_div() {
//        vm.dstk.push(10, 2)
//        mod.w_div(vm)
//        assertDStack(5)
//    }
//
//    @Test
//    fun w_mod() {
//        vm.dstk.push(10, 2)
//        mod.w_mod(vm)
//        assertDStack(0)
//
//        vm.dstk.push(11, 2)
//        mod.w_mod(vm)
//        assertDStack(1)
//    }
//
//    @Test
//    fun w_eq() {
//        vm.dstk.push(10, 20)
//        mod.w_eq(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(10, 10)
//        mod.w_eq(vm)
//        assertDStack(TRUE)
//    }
//
//    @Test
//    fun w_gt() {
//        vm.dstk.push(10, 20)
//        mod.w_gt(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(10, 10)
//        mod.w_gt(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(20, 10)
//        mod.w_gt(vm)
//        assertDStack(TRUE)
//    }
//
//    @Test
//    fun w_lt() {
//        vm.dstk.push(10, 20)
//        mod.w_lt(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(10, 10)
//        mod.w_lt(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(20, 10)
//        mod.w_lt(vm)
//        assertDStack(FALSE)
//    }
//
//    @Test
//    fun w_gte() {
//        vm.dstk.push(10, 20)
//        mod.w_gte(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(10, 10)
//        mod.w_gte(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(20, 10)
//        mod.w_gte(vm)
//        assertDStack(TRUE)
//    }
//
//    @Test
//    fun w_lte() {
//        vm.dstk.push(10, 20)
//        mod.w_lte(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(10, 10)
//        mod.w_lte(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(20, 10)
//        mod.w_lte(vm)
//        assertDStack(FALSE)
//    }
//
//    @Test
//    fun w_inc() {
//        vm.dstk.push(10)
//        mod.w_inc(vm)
//        assertDStack(11)
//    }
//
//    @Test
//    fun w_dec() {
//        vm.dstk.push(10)
//        mod.w_dec(vm)
//        assertDStack(9)
//    }
//
//    @Test
//    fun w_ne() {
//        vm.dstk.push(10, 20)
//        mod.w_ne(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(10, 10)
//        mod.w_ne(vm)
//        assertDStack(FALSE)
//    }
//
//    @Test
//    fun w_eq0() {
//        vm.dstk.push(0)
//        mod.w_eq0(vm)
//        assertDStack(TRUE)
//
//        vm.dstk.push(-1)
//        mod.w_eq0(vm)
//        assertDStack(FALSE)
//
//        vm.dstk.push(42)
//        mod.w_eq0(vm)
//        assertDStack(FALSE)
//    }
//
//    @Test
//    fun w_sqrt() {
//        vm.dstk.push(16)
//        mod.w_sqrt(vm)
//        assertDStack(4)
//
//        vm.dstk.push(17)
//        mod.w_sqrt(vm)
//        assertDStack(4)
//    }
//
//    @Test
//    fun w_true() {
//        mod.w_true(vm)
//        assertDStack(TRUE)
//    }
//
//    @Test
//    fun w_false() {
//        mod.w_false(vm)
//        assertDStack(FALSE)
//    }
//}