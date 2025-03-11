import kf.ForthVM
import kf.ParseError
import kf.WComments
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*
import kotlin.test.assertFailsWith

class WCommentsTest : ForthTestCase() {
    val comments = WComments(vm)

    @Test
    fun w_parenComment() {
        vm.interpScanner = Scanner("( test )")
        comments.w_parenComment()
        assertEquals(false, vm.interpScanner!!.hasNext())

        vm.interpScanner = Scanner("( test )   foo")
        comments.w_parenComment()
        assertEquals("foo", vm.interpScanner!!.next())

        vm.interpScanner = Scanner("( test    foo")
        assertFailsWith<ParseError> { comments.w_parenComment() }
    }

    @Test
    fun w_backslashComment() {
        vm.interpScanner = Scanner("\\ test )")
        comments.w_backslashComment()
        assertEquals(false, vm.interpScanner!!.hasNext())

        vm.interpScanner = Scanner("   \\ ( test )")
        comments.w_backslashComment()
        assertEquals(false, vm.interpScanner!!.hasNext())

        vm.interpScanner = Scanner("  foo  \\ ( test )")
        vm.interpScanner!!.next()
        comments.w_backslashComment()
        assertEquals(false, vm.interpScanner!!.hasNext())
    }
}