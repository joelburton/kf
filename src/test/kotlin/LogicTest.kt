import kf.ForthVM.Companion.FALSE
import kf.ForthVM.Companion.TRUE
import org.junit.jupiter.api.Test

class WLogicTest : EvalForthTestCase() {
    val mod = kf.words.core.wLogic
    val ext = kf.words.core.ext.wLogicExt
    val custom = kf.words.custom.wLogicCustom

    @Test
    fun w_and() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_and(vm)

        assertDStack(0b0100)
    }

    @Test
    fun w_or() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_or(vm)
        assertDStack(0b0101)
    }

    @Test
    fun w_not() {
        vm.dstk.push(0b0101)
        custom.w_not(vm)
        assertDStack(0b1111111111111111111111111111010)
    }

    @Test
    fun w_xor() {
        vm.dstk.push(0b0101, 0b0100)
        mod.w_xor(vm)
        assertDStack(0b0001)
    }

    @Test
    fun w_invert() {
        vm.dstk.push(0b0101)
        mod.w_invert(vm)
        assertDStack(0b1111111111111111111111111111010)
    }


    @Test
    fun w_equals() {
        vm.dstk.push(10, 20)
        mod.w_equals(vm)
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_equals(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_gt() {
        vm.dstk.push(10, 20)
        mod.w_gt(vm)
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        mod.w_gt(vm)
        assertDStack(FALSE)

        vm.dstk.push(20, 10)
        mod.w_gt(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_lt() {
        vm.dstk.push(10, 20)
        mod.w_lessThan(vm)
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        mod.w_lessThan(vm)
        assertDStack(FALSE)

        vm.dstk.push(20, 10)
        mod.w_lessThan(vm)
        assertDStack(FALSE)
    }

    @Test
    fun w_gte() {
        vm.dstk.push(10, 20)
        custom.w_gte(vm)
        assertDStack(FALSE)

        vm.dstk.push(10, 10)
        custom.w_gte(vm)
        assertDStack(TRUE)

        vm.dstk.push(20, 10)
        custom.w_gte(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_lte() {
        vm.dstk.push(10, 20)
        custom.w_lte(vm)
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        custom.w_lte(vm)
        assertDStack(TRUE)

        vm.dstk.push(20, 10)
        custom.w_lte(vm)
        assertDStack(FALSE)
    }

    @Test
    fun w_ne() {
        vm.dstk.push(10, 20)
        ext.w_ne(vm)
        assertDStack(TRUE)

        vm.dstk.push(10, 10)
        ext.w_ne(vm)
        assertDStack(FALSE)
    }

    @Test
    fun w_eq0() {
        vm.dstk.push(0)
        mod.w_zeroEquals(vm)
        assertDStack(TRUE)

        vm.dstk.push(-1)
        mod.w_zeroEquals(vm)
        assertDStack(FALSE)

        vm.dstk.push(42)
        mod.w_zeroEquals(vm)
        assertDStack(FALSE)
    }

    @Test
    fun w_true() {
        ext.w_true(vm)
        assertDStack(TRUE)
    }

    @Test
    fun w_false() {
        ext.w_false(vm)
        assertDStack(FALSE)
    }
}