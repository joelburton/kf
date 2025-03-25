package words.core.ext

import ForthTestCase
import kf.words.core.ext.wStackOpsExt
import org.junit.jupiter.api.Test

class wStackOpsExtTest : ForthTestCase() {
    val mod = wStackOpsExt

    @Test
    fun w_roll() {
        vm.dstk.push(10, 20, 30, 40)
        vm.dstk.push(2)
        mod.w_roll(vm)
        assertDStack(10, 30, 40, 20)
    }

    @Test
    fun w_pick() {
        vm.dstk.push(10, 20, 30, 40)
        vm.dstk.push(2)
        mod.w_pick(vm)
        assertDStack(10, 20, 30, 40, 20)
    }

    @Test
    fun w_nip() {
        vm.dstk.push(10, 20)
        mod.w_nip(vm)
        assertDStack(20)
    }


    @Test
    fun w_tuck() {
        vm.dstk.push(10, 20)
        mod.w_tuck(vm)
        assertDStack(20, 10, 20)
    }

}