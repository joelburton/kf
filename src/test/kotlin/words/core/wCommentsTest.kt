package words.core

import ForthTestCase
import kf.words.core.wComments
import kotlin.test.Test
import kotlin.test.assertEquals

class wCommentsTest : ForthTestCase() {
    val mod = wComments

    @Test
    fun w_parenComment() {
        vm.interp.scanner.fill("( test )")
        mod.w_parenComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)

        vm.interp.scanner.fill("( test )   foo")
        mod.w_parenComment(vm)
        assertEquals(false, vm.interp.scanner.atEnd)

        vm.interp.scanner.fill("( test    foo")
        mod.w_parenComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)
    }
}