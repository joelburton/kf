package words.core

import ForthTestCase
import kf.words.core.wDoubleCell
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wDoubleCellTest : ForthTestCase() {
    val mod = wDoubleCell

    @Test
    fun w_mStar() {
        vm.dstk.push(10, 5)
        mod.w_mStar(vm)
        val long = vm.dstk.dblPop()
        assertEquals(50, long)

        vm.dstk.push(-10, 5)
        mod.w_mStar(vm)
        val longNeg = vm.dstk.dblPop()
        assertEquals(-50, longNeg)
    }

    @Test
    fun w_sToD() {
        vm.dstk.push(10)
        mod.w_sToD(vm)
        val long = vm.dstk.dblPop()
        assertEquals(10, long)

        vm.dstk.push(-10)
        mod.w_sToD(vm)
        val longNeg = vm.dstk.dblPop()
        assertEquals(-10, longNeg)

    }
}