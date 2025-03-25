package words.core

import ForthTestCase
import kf.words.core.wStacks
import org.junit.jupiter.api.Test

class wRStackTest : ForthTestCase() {
    val mod = wStacks

    @Test
    fun w_toR() {
        vm.dstk.push(10)
        mod.w_toR(vm)
        assertRStack(10)
        assertDStack()
    }

    @Test
    fun w_rFrom() {
        vm.rstk.push(10)
        mod.w_rFrom(vm)
        assertDStack(10)
        assertRStack()
    }

    @Test
    fun w_rFetch() {
        vm.rstk.push(10)
        mod.w_rFetch(vm)
        assertDStack(10)
        assertRStack(10)
    }

}