package words.core

import ForthTestCase
import dummyFn
import kf.Word
import kf.words.core.wWords
import org.junit.jupiter.api.Test

class wWordsTest : ForthTestCase() {
    val mod = wWords

    @Test
    fun w_bracketTick() {
        vm.source.scanner.fill("BRK")
        mod.w_tick(vm)
        assertDStack(0)
    }

    @Test
    fun w_tick() {
        vm.source.scanner.fill("BRK")
        mod.w_tick(vm)
        assertDStack(0)
    }

    @Test
    fun w_find() {
        vm.dend = 0x200
        vm.appendStrToData("BRK")
        vm.dstk.push(0x200) // xt for BRK
        mod.w_find(vm)
        assertDStack(0, -1)

        vm.dend = 0x200
        vm.appendStrToData("XXX-NOPE")
        vm.dstk.push(0x200) // xt for BRK
        mod.w_find(vm)
        assertDStack(0x200, 0)
    }

    @Test
    fun w_toBody() {
        val w = Word("X", ::dummyFn, dpos = 42)
        vm.dict.add(w)
        vm.dstk.push(w.wn)
        mod.w_toBody(vm)
        assertDStack(42)
    }

}