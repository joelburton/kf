package words.core

import ForthTestCase
import kf.words.core.wMemory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wMemoryTest : ForthTestCase() {
    val mod = wMemory

    @Test
    fun w_fetch() {
        vm.mem[0x200] = 42
        vm.dstk.push(0x200)
        mod.w_fetch(vm)
        assertDStack(42)
    }

    @Test
    fun w_store() {
        vm.dstk.push(45, 0x201)
        mod.w_store(vm)
        assertEquals(45, vm.mem[0x201])
    }

    @Test
    fun w_plusStore() {
        vm.mem[0x200] = 10
        vm.dstk.push(2, 0x200)
        mod.w_plusStore(vm)
        assertEquals(12, vm.mem[0x200])
    }

    @Test
    fun w_twoFetch() {
        vm.mem[0x200] = 10
        vm.mem[0x201] = 20
        vm.dstk.push(0x200)
        mod.w_twoFetch(vm)
        assertDStack(10, 20)
    }

    @Test
    fun w_twoStore() {
        vm.dstk.push(10, 20, 0x200)
        mod.w_twoStore(vm)
        assertEquals(20, vm.mem[0x200])
        assertEquals(10, vm.mem[0x201])
    }

    @Test
    fun w_comma() {
        vm.dend = 0x205
        vm.dstk.push(10)
        mod.w_comma(vm)
        assertEquals(0x206, vm.dend)
        vm.mem[0x205] = 10
    }

    @Test
    fun w_here() {
        vm.dend = 0x201
        mod.w_here(vm)
        assertDStack(0x201)
    }

    @Test
    fun w_fill() {
        vm.dstk.push(0x100, 5, 65)
        mod.w_fill(vm)
        for (i in 0 until 5) {
            assertEquals(65, vm.mem[0x100 + i])
        }
    }

    @Test
    fun w_move() {
        vm.mem[0x100] = 10
        vm.mem[0x101] = 20
        vm.mem[0x102] = 30
        vm.mem[0x103] = 40
        vm.mem[0x104] = 50
        vm.dstk.push(0x100, 0x102, 5)
        mod.w_move(vm)
        assertEquals(10, vm.mem[0x100])
        assertEquals(20, vm.mem[0x101])

        vm.mem[0x102] = 10
        vm.mem[0x103] = 20
        vm.mem[0x104] = 30
        vm.mem[0x105] = 40
        vm.mem[0x106] = 50
        vm.dstk.push(0x102, 0x100, 5)
        mod.w_move(vm)
        assertEquals(10, vm.mem[0x100])
        assertEquals(20, vm.mem[0x101])
    }

    @Test
    fun w_allot() {
        vm.dend = 0
        vm.dstk.push(10)
        mod.w_allot(vm)
        assertEquals(10, vm.dend)
    }

    @Test
    fun w_cellPlus() {
        vm.dstk.push(10)
        mod.w_cellPlus(vm)
        assertDStack(10 + 1)
    }

    @Test
    fun w_cells() {
        vm.dstk.push(10)
        mod.w_cells(vm)
        assertDStack(10 * 1)
    }

    @Test
    fun w_align() {
        vm.dend = 0x1ff
        vm.dstk.push(0x1ff)
        mod.w_align(vm)
        assertEquals(0x1ff, vm.dend)
    }

    @Test
    fun w_aligned() {
        vm.dend = 0x1ff
        vm.dstk.push(0x1ff)
        mod.w_aligned(vm)
        assertDStack(0x1ff)
    }

}