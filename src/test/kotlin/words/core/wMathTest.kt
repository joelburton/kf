package words.core

import ForthTestCase
import kf.words.core.wMath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wMathTest : ForthTestCase() {
    val mod = wMath

    @Test
    fun w_plus() {
        vm.dstk.push(10, 20)
        mod.w_plus(vm)
        assertDStack(30)
    }

    @Test
    fun w_minus() {
        vm.dstk.push(10, 20)
        mod.w_minus(vm)
        assertDStack(-10)
    }

    @Test
    fun w_star() {
        vm.dstk.push(10, 20)
        mod.w_star(vm)
        assertDStack(200)
    }

    @Test
    fun w_slash() {
        vm.dstk.push(10, 2)
        mod.w_slash(vm)
        assertDStack(5)
    }

    @Test
    fun w_negate() {
        vm.dstk.push(5)
        mod.w_negate(vm)
        assertDStack(-5)

        vm.dstk.push(-5)
        mod.w_negate(vm)
        assertDStack(5)
    }

    @Test
    fun w_lshift() {
        vm.dstk.push(10, 2)
        mod.w_lshift(vm)
        assertDStack(40)
    }

    @Test
    fun w_rshift() {
        vm.dstk.push(40, 2)
        mod.w_rshift(vm)
        assertDStack(10)
    }

    @Test
    fun w_abs() {
        vm.dstk.push(-10)
        mod.w_abs(vm)
        assertDStack(10)
    }

    @Test
    fun w_twoStar() {
        vm.dstk.push(10)
        mod.w_twoStar(vm)
        assertDStack(20)
    }

    @Test
    fun w_twoSlash() {
        vm.dstk.push(10)
        mod.w_twoSlash(vm)
        assertDStack(5)
    }

    @Test
    fun w_starSlash() {
        vm.dstk.push(10, 2, 5)
        mod.w_starSlash(vm)
        assertDStack(4)
    }

    @Test
    fun w_mod() {
        vm.dstk.push(10, 2)
        mod.w_mod(vm)
        assertDStack(0)

        vm.dstk.push(11, 2)
        mod.w_mod(vm)
        assertDStack(1)
    }

    @Test
    fun w_slashMod() {
        vm.dstk.push(21, 10)
        mod.w_slashMod(vm)
        assertDStack(1, 2)
    }

    @Test
    fun w_starSlashMod() {
        vm.dstk.push(10, 2, 3)
        mod.w_starSlashMod(vm)
        assertDStack(2, 6)
    }

    @Test
    fun w_fmSlashMod() {
        vm.dstk.dblPush(-7)
        vm.dstk.push(3)
        mod.w_fmSlashMod(vm)
        assertDStack(-3, 2)
    }

    @Test
    fun w_smSlashRem() {
        vm.dstk.dblPush(-7)
        vm.dstk.push(3)
        mod.w_smSlashRem(vm)
        assertDStack(-2, -1)
    }

    @Test
    fun w_onePlus() {
        vm.dstk.push(10)
        mod.w_onePlus(vm)
        assertDStack(11)
    }

    @Test
    fun w_oneMinus() {
        vm.dstk.push(10)
        mod.w_oneMinus(vm)
        assertDStack(9)
    }
}