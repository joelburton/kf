import org.junit.jupiter.api.Test

class WMathTest : ForthTestCase() {
    val mod = kf.words.core.wMath


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
    fun w_mod() {
        vm.dstk.push(10, 2)
        mod.w_mod(vm)
        assertDStack(0)

        vm.dstk.push(11, 2)
        mod.w_mod(vm)
        assertDStack(1)
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

    @Test
    fun w_sqrt() {
        vm.dstk.push(16)
        // wrong place for this
        kf.words.custom.wLogicCustom.w_sqrt(vm)
        assertDStack(4)

        vm.dstk.push(17)
        kf.words.custom.wLogicCustom.w_sqrt(vm)
        assertDStack(4)
    }
}