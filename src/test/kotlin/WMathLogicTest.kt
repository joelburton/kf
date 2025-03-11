import kf.WMathLogic
import org.junit.jupiter.api.Test
import kf.ForthVM.Companion.TRUE
import kf.ForthVM.Companion.FALSE

class WMathLogicTest : ForthTestCase() {
    val mod: WMathLogic

    init {
        mod = vm.modulesLoaded["MathLogic"]!! as WMathLogic
    }

    @Test
    fun w_and() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_and()
        
        assertDStack(0b0100)
    }

    @Test
    fun w_or() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_or()
        assertDStack(0b0101)
    }

    @Test
    fun w_not() {
        vm.dstk.push(0b0101)
        mod.w_not()
        assertDStack(0b1111111111111111111111111111010)
    }

    @Test
    fun w_xor() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_xor()
        assertDStack(0b0001)
    }

    @Test
    fun w_negate() {
        vm.dstk.push(5)
        mod.w_negate()
        assertDStack(-5)

        vm.dstk.push(-5)
        mod.w_negate()
        assertDStack(5)
    }

    @Test
    fun w_inv() {
        vm.dstk.push(0b0101)
        mod.w_inv()
        assertDStack(0b1111111111111111111111111111010)
    }

    @Test
    fun w_add() {
        vm.dstk.push(10, 20)
        mod.w_add()
        assertDStack(30)
    }

    @Test
    fun w_sub() {
        vm.dstk.push(10, 20)
        mod.w_sub()
        assertDStack(-10)
    }

    @Test
    fun w_mul() {
        vm.dstk.push(10, 20)
        mod.w_mul()
        assertDStack(200)
    }

    @Test
    fun w_div() {
        vm.dstk.push(10, 2)
        mod.w_div()
        assertDStack(5)
    }

    @Test
    fun w_mod() {
        vm.dstk.push(10, 2)
        mod.w_mod()
        assertDStack(0)

        vm.dstk.push(11, 2)
        mod.w_mod()
        assertDStack(1)
    }

    @Test
    fun w_eq() {
        vm.dstk.push(10, 20)
        mod.w_eq()
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_eq()
        assertDStack(TRUE)
    }

    @Test
    fun w_gt() {
        vm.dstk.push(10, 20)
        mod.w_gt()
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_gt()
        assertDStack(FALSE)

        vm.dstk.push(20, 10)
        mod.w_gt()
        assertDStack(TRUE)
    }

    @Test
    fun w_lt() {
        vm.dstk.push(10, 20)
        mod.w_lt()
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        mod.w_lt()
        assertDStack(FALSE)

        vm.dstk.push(20, 10)
        mod.w_lt()
        assertDStack(FALSE)
    }

    @Test
    fun w_gte() {
        vm.dstk.push(10, 20)
        mod.w_gte()
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_gte()
        assertDStack(TRUE)

        vm.dstk.push(20, 10)
        mod.w_gte()
        assertDStack(TRUE)
    }

    @Test
    fun w_lte() {
        vm.dstk.push(10, 20)
        mod.w_lte()
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        mod.w_lte()
        assertDStack(TRUE)

        vm.dstk.push(20, 10)
        mod.w_lte()
        assertDStack(FALSE)

    }

    @Test
    fun w_inc() {
        vm.dstk.push(10)
        mod.w_inc()
        assertDStack(11)
    }

    @Test
    fun w_dec() {
        vm.dstk.push(10)
        mod.w_dec()
        assertDStack(9)
    }

    @Test
    fun w_ne() {
        vm.dstk.push(10, 20)
        mod.w_ne()
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        mod.w_ne()
        assertDStack(FALSE)
    }

    @Test
    fun w_eq0() {
        vm.dstk.push(0)
        mod.w_eq0()
        assertDStack(TRUE)

        vm.dstk.push(-1)
        mod.w_eq0()
        assertDStack(FALSE)

        vm.dstk.push(42)
        mod.w_eq0()
        assertDStack(FALSE)
    }

    @Test
    fun w_sqrt() {
        vm.dstk.push(16)
        mod.w_sqrt()
        assertDStack(4)

        vm.dstk.push(17)
        mod.w_sqrt()
        assertDStack(4)
    }

    @Test
    fun w_true() {
        mod.w_true()
        assertDStack(TRUE)
    }

    @Test
    fun w_false() {
        mod.w_false()
        assertDStack(FALSE)
    }
}