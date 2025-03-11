package primitives

import ForthTestCase
import kf.WInternals
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class WInternalsTest : ForthTestCase() {
    val mod: WInternals

    init {
        mod = vm.modulesLoaded["Internals"]!! as WInternals
    }

    @Test
    fun w_ipLoad() {
        mod.w_ipLoad()
        assertEquals(vm.cstart, vm.ip)
    }

    @Test
    fun w_ipStore() {
        vm.dstk.push(42)
        mod.w_ipStore()
        assertEquals(42, vm.ip)
    }
}