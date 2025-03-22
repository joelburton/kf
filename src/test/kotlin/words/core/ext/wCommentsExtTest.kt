package words.core.ext

import ForthTestCase
import kf.words.core.ext.wCommentsExt
import kotlin.test.Test
import kotlin.test.assertEquals

class wCommentsExtTest : ForthTestCase() {
    val mod = wCommentsExt

    @Test
    fun w_backslashComment() {
        vm.scanner.fill("\\ test )")
        mod.w_backslashComment(vm)
        assertEquals(true, vm.scanner.atEnd)

        vm.scanner.fill("   \\ ( test )")
        mod.w_backslashComment(vm)
        assertEquals(true, vm.scanner.atEnd)

        vm.scanner.fill("  foo  \\ ( test )")
        vm.scanner.parseName()
        mod.w_backslashComment(vm)
        assertEquals(true, vm.scanner.atEnd)
    }
}