import kf.words.core.ext.wCommentsExt
import kf.words.core.wComments
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WCommentsTest : ForthTestCase() {
    val comments = wComments
    val ext = wCommentsExt
    
    @Test
    fun w_parenComment() {
        vm.interp.scanner.fill("( test )")
        comments.w_parenComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)

        vm.interp.scanner.fill("( test )   foo")
        comments.w_parenComment(vm)
        assertEquals(false, vm.interp.scanner.atEnd)

        vm.interp.scanner.fill("( test    foo")
        comments.w_parenComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)
    }

    @Test
    fun w_backslashComment() {
        vm.interp.scanner.fill("\\ test )")
        ext.w_backslashComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)

        vm.interp.scanner.fill("   \\ ( test )")
        ext.w_backslashComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)

        vm.interp.scanner.fill("  foo  \\ ( test )")
        vm.interp.scanner.parseName()
        ext.w_backslashComment(vm)
        assertEquals(true, vm.interp.scanner.atEnd)
    }
}