package words.core.ext

import ForthTestCase
import kf.words.core.ext.wStacksExt
import org.junit.jupiter.api.Test

class wStacksExtTest : ForthTestCase() {
    val mod = wStacksExt

    @Test
    fun w_twoToR() {
        vm.dstk.push(10, 20, 30)
        mod.w_twoToR(vm)
        assertDStack(10)
        assertRStack(20, 30)
    }

    @Test
    fun w_twoRFrom() {
        vm.rstk.push(10, 20, 30)
        mod.w_twoRFrom(vm)
        assertDStack(20, 30)
        assertRStack(10)
    }

    @Test
    fun w_twoRFetch() {
        vm.rstk.push(10, 20, 30)
        mod.w_twoRFetch(vm)
        assertDStack(20, 30)
        assertRStack(10, 20, 30)
    }

}