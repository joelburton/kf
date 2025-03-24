package words.core.ext

import ForthTestCase
import kf.words.core.ext.wCommentsExt
import kotlin.test.Test
import kotlin.test.assertEquals

class wCommentsExtTest : ForthTestCase() {
    val mod = wCommentsExt

    @Test
    fun w_backslashComment() {
        vm.source.scanner.fill("\\ test )")
        mod.w_backslashComment(vm)
        assertEquals(true, vm.source.scanner.atEnd)

        vm.source.scanner.fill("   \\ ( test )")
        mod.w_backslashComment(vm)
        assertEquals(true, vm.source.scanner.atEnd)

        vm.source.scanner.fill("  foo  \\ ( test )")
        vm.source.scanner.parseName()
        mod.w_backslashComment(vm)
        assertEquals(true, vm.source.scanner.atEnd)
    }
}