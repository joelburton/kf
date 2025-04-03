package words.core

import ForthTestCase
import kf.interps.FScanner
import kf.words.core.wComments
import kotlin.test.Test
import kotlin.test.assertEquals

class wCommentsTest : ForthTestCase() {
    val mod = wComments

    @Test
    fun w_parenComment() {
        vm.source.scanner.fill("( test )")
        mod.w_parenComment(vm)
        assertEquals(true, (vm.source.scanner as FScanner).atEnd)

        vm.source.scanner.fill("( test )   foo")
        mod.w_parenComment(vm)
        assertEquals(false, (vm.source.scanner as FScanner).atEnd)

        vm.source.scanner.fill("( test    foo")
        mod.w_parenComment(vm)
        assertEquals(true, (vm.source.scanner as FScanner).atEnd)
    }
}