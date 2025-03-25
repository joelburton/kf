package words.core.ext

import ForthTestCase
import kf.words.core.ext.wNumIOExt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wNumIOExtTest : ForthTestCase() {
    val mod = wNumIOExt

    @Test
    fun w_hex() {
        vm.base = 2
        mod.w_hex(vm)
        assertEquals(16, vm.base)
    }

    @Test
    fun w_dotR() {
        vm.base = 10
        vm.dstk.push(16, 5)
        mod.w_dotR(vm)
        assertDStack()
        assertPrinted("   16")

        vm.base = 16
        vm.dstk.push(10, 5)
        mod.w_dotR(vm)
        assertDStack()
        assertPrinted("    a")
    }

}