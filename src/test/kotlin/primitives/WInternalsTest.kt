package primitives

import ForthTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WInternalsTest : ForthTestCase() {
    val mod = kf.words.custom.wToolsCustom

    @Test
    fun xw_ipLoad() {
        mod.w_dotIPFetch(vm)
        assertEquals(vm.cstart, vm.ip)
    }

    @Test
    fun w_ipStore() {
        vm.dstk.push(42)
        mod.w_dotIPStore(vm)
        assertEquals(42, vm.ip)
    }
}