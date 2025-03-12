import kf.InvalidState
import kf.ParseError
import kf.StackOverflow
import kf.WCompiling
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class WCompilingTest : ForthTestCase() {
    val mod: WCompiling

    init {
        mod = vm.modulesLoaded["Compiling"]!! as WCompiling
    }

    @Test
    fun w_colon() {
        // integration test
        eval(": test 123 ;")
        assertNotNull(vm.dict["test"])
        eval("test")
        assertDStack(123)
        assertFailsWith<InvalidState> { eval(": a : test1 123 ;") }
    }

    @Test
    fun w_semicolon() {
        // integration test
        eval(": test 123 ;")
        assertNotNull(vm.dict["test"])
        eval("test")
        assertDStack(123)
        assertFailsWith<InvalidState> { eval(": test1 123 ; ;") }
    }

    @Test
    fun w_doLit() {
        mod.w_doLit()
        assertEquals(vm.dict.getNum("lit"), vm.dstk.pop())
    }

    @Test
    fun w_bracketTick() {
        assertFailsWith<InvalidState> { eval("['] 123") }
        vm.reset()

        eval(": test ['] dup ; test")
        assertEquals(vm.dict.getNum("dup"), vm.dstk.pop())
    }

    @Test
    fun w_immediate() {
        // normal func
        eval(": f1 42 ;")
        assertDStack()
        eval(": use1 f1 ;")
        assertDStack()

        // immediate
        eval(": f2 immediate 42 ;")
        assertDStack()
        eval(": use2 f2 42 ;")
        assertDStack(42)

        // immediate
        eval(": f3 42 ; immediate")
        assertDStack()
        eval(": use3 f3 42 ;")
        assertDStack(42)
    }

    @Disabled("is slow, since it nees a stack overflow")
    @Test
    fun w_recursive() {
        // normal func
        assertFailsWith<ParseError> { eval(": f1 f1 ;") }

        // normal func w/predecessor
        eval(": f2 42 ; : f2 f2 ; f2")
        assertDStack(42)

        // recursive func
        assertFailsWith<StackOverflow> { eval(": f3 recursive 42 f3 ; f3") }
    }

    @Test
    fun w_postpone() {
    }

    @Test
    fun w_bracketLiteral() {
        assertFailsWith<InvalidState> { eval("[literal] 123") }
        vm.reset()

        var initCend = vm.cend
        eval(": test 123 [literal] ;") // lit,123,[literal],return
        assertEquals(4, vm.cend - initCend)
        assertEquals(vm.dict.getNum("lit"), vm.mem[vm.cend - 4])
        assertEquals(123, vm.mem[vm.cend - 3])
        assertEquals(vm.dict.getNum("[literal]"), vm.mem[vm.cend - 2])
        assertEquals(vm.dict.getNum("return"), vm.mem[vm.cend - 1])

        initCend = vm.cend
        eval(": test2 test ;")
        see("test2")
        assertEquals(2, vm.cend - initCend)
        assertEquals(vm.dict.getNum("test"), vm.mem[vm.cend - 2])

        initCend = vm.cend
        eval(": test immediate 123 [literal] ;") // lit,123,[literal],return
        assertEquals(4, vm.cend - initCend)
        assertEquals(vm.dict.getNum("lit"), vm.mem[vm.cend - 4])
        assertEquals(123, vm.mem[vm.cend - 3])
        assertEquals(vm.dict.getNum("[literal]"), vm.mem[vm.cend - 2])
        assertEquals(vm.dict.getNum("return"), vm.mem[vm.cend - 1])

        initCend = vm.cend
        eval(": test2 test ;")
        see("test2")
        assertEquals(3, vm.cend - initCend)
        assertEquals(vm.dict.getNum("lit"), vm.mem[vm.cend - 3])
        assertEquals(123, vm.mem[vm.cend - 2])
    }

    @Test
    fun w_literal() {
        assertFailsWith<InvalidState> { eval("123 literal") }
        vm.reset()

        var initialCend = vm.cend
        eval(": test [ 2 4 * ] literal ;") // lit,8,return
        assertEquals(3, vm.cend - initialCend)
        assertEquals(vm.dict.getNum("lit"), vm.mem[vm.cend - 3])
        assertEquals(8, vm.mem[vm.cend - 2])
    }
}