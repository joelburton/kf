package words.core

import EvalForthTestCase
import ForthTestCase
import kf.words.core.wLoops
import kf.words.machine.wMachine
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wLoopsTest : ForthTestCase() {
    val mod = wLoops

    init {
        vm.dict.addModule(wMachine)
    }

    @Test
    fun w_begin() {
        vm.cend = vm.cstart
        mod.w_begin(vm)
        assertRStack(vm.cstart)
    }

    @Test
    fun w_until() {
        vm.cend = vm.cstart
        mod.w_begin(vm)
        mod.w_until(vm)
        assertEquals(vm.dict["0BRANCH"].wn, vm.mem[vm.cend - 2])
        assertEquals(-1, vm.mem[vm.cend - 1])
        assertRStack()
    }

    @Test
    fun w_while() {
        vm.cend = vm.cstart
        mod.w_begin(vm)
        mod.w_while(vm)
        assertEquals(vm.dict["0BRANCH"].wn, vm.mem[vm.cend - 2])
        assertEquals(0xffff, vm.mem[vm.cend - 1])
        assertRStack(vm.cstart, vm.cstart + 2)
    }

    @Test
    fun w_repeat() {
        vm.cend = vm.cstart
        mod.w_begin(vm)
        mod.w_until(vm)
        assertEquals(vm.dict["0BRANCH"].wn, vm.mem[vm.cend - 2])
        assertEquals(-1, vm.mem[vm.cend - 1])
        assertRStack()
    }

}


class wLoopsFuncTest : EvalForthTestCase() {
    @Test
    fun doLoop() {
        eval(": test 3 0 do i 1 + loop ; test")
        assertDStack(1, 2, 3)
    }

    @Test
    fun doPlusLoop() {
        eval(": test 6 0 do i 2 +loop ; test")
        assertDStack(0, 2, 4)
    }

    @Test
    fun doLeave() {
        eval(": test 3 0 do i 2 = if leave then i 1 + loop ;")
        eval("test")
        assertDStack(1, 2)

        eval(": t 3 0 do i 3 = if leave then i 2 = if leave then i 1 + loop ;")
        eval("t")
        assertDStack(1, 2)

        eval(": test 3 0 do i 2 = if leave then i 1 + 1 +loop ;")
        eval("test")
        assertDStack(1, 2)

        eval(
            ": t 3 0 do i 3 = if leave then i 2 = if leave then i 1 + 1 +loop ;")
        eval("t")
        assertDStack(1, 2)
    }

    @Test
    fun until() {
        eval(": test begin 1- dup dup 0= until drop ;")
        eval("3 test")
        assertDStack(2, 1, 0)
    }

    @Test
    fun iJ() {
        eval(": t 2 0 do i 12 10 do i j loop i loop ;")
        eval("t")
        assertDStack(
            0, 10, 0, 11, 0, 0,
            1, 10, 1, 11, 1, 1,
        )
    }

    @Test
    fun unloop() {
        eval(": t 2 0 do 12 10 do unloop unloop exit loop loop ;")
        see("t")
        eval("t")
        assertDStack()
        assertRStack()
    }
}