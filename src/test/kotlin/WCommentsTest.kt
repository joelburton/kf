import kf.ParseError
import kf.primitives.WComments
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

class WCommentsTest : ForthTestCase() {
    val comments = WComments

    @Test
    fun w_parenComment() {
        vm.interpScanner.fill("( test )")
        comments.w_parenComment(vm)
        assertEquals(true, vm.interpScanner.atEnd)

        vm.interpScanner.fill("( test )   foo")
        comments.w_parenComment(vm)
        assertEquals(false, vm.interpScanner.atEnd)

        vm.interpScanner.fill("( test    foo")
        assertFailsWith<ParseError> { comments.w_parenComment(vm) }
    }

    @Test
    fun w_backslashComment() {
        vm.interpScanner.fill("\\ test )")
        comments.w_backslashComment(vm)
        assertEquals(true, vm.interpScanner.atEnd)

        vm.interpScanner.fill("   \\ ( test )")
        comments.w_backslashComment(vm)
        assertEquals(true, vm.interpScanner.atEnd)

        vm.interpScanner.fill("  foo  \\ ( test )")
        vm.interpScanner.parseName()
        comments.w_backslashComment(vm)
        assertEquals(true, vm.interpScanner.atEnd)
    }
}