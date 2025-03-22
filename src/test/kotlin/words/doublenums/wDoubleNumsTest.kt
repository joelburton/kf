package words.doublenums

import ForthTestCase
import kf.words.doublenums.wDoubleNums
import org.junit.jupiter.api.Test

class wDoubleNumsTest : ForthTestCase() {
    val mod = wDoubleNums

    @Test
    fun w_dDot() {
        vm.dstk.dblPush(42)
        vm.base = 10
        mod.w_dDot(vm)
        assertPrinted("42 ")

        vm.dstk.dblPush(-42)
        vm.base = 10
        mod.w_dDot(vm)
        assertPrinted("-42 ")
    }
}