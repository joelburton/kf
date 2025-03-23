package words.core.ext

import ForthTestCase
import kf.words.core.ext.wMemoryExt
import kf.words.core.wCreate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wMemoryExtTest : ForthTestCase() {
    val mod = wMemoryExt

    init {
        vm.dict.addModule(wCreate)
    }

    @Test
    fun w_bufferColon() {
        vm.scanner.fill("foo")
        vm.dstk.push(5)
        val dend = vm.dend
        mod.w_bufferColon(vm)
        assertEquals(dend + 5, vm.dend )
        assertNotNull(vm.dict["foo"])
    }

    @Test
    fun w_pad() {
        mod.w_pad(vm)
        assertDStack(vm.memConfig.padStart)
    }

    @Test
    fun w_erase() {
        vm.mem[0x00] = 65
        vm.mem[0x01] = 65
        vm.mem[0x02] = 65
        vm.dstk.push(0, 2)
        mod.w_erase(vm)
        assertEquals(0, vm.mem[0x00])
        assertEquals(0, vm.mem[0x01])
        assertEquals(65, vm.mem[0x02])
    }

    @Test
    fun w_unused() {
        vm.dend = vm.memConfig.dataStart + 10
        mod.w_unused(vm)
        assertDStack(vm.memConfig.dataEnd - vm.dend)
    }

}