package words.core.ext

import EvalForthTestCase
import ForthTestCase
import kf.words.core.ext.wLoopsExt
import kf.words.core.wLoops
import kf.words.core.wLoops.w_begin
import kf.words.machine.wMachine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class wLoopsExtTest : ForthTestCase() {
    val mod = wLoopsExt

    init {
        vm.dict.addModule(wMachine)
        vm.dict.addModule(wLoops)
    }

    @Test
    fun w_again() {
        vm.cend = 0x100
        w_begin(vm)
        mod.w_again(vm)
        assertEquals(vm.dict["branch"].wn, vm.mem[vm.cend - 2])
        assertEquals(-1, vm.mem[vm.cend - 1])
    }
}


class wLoopsExtFuncTest : EvalForthTestCase() {
    @Test
    fun questionDo() {
        eval(": a 4 1 ?do i loop ;")
        see("a")
        eval("a")
        assertDStack(1, 2, 3)

        eval(": a 1 1 do i loop ; a")
        assertDStack(1)

        eval(": a 1 1 ?do i loop ; a")
        assertDStack()
    }
}