import kf.InvalidState
import kf.ParseError
import kf.StackOverflow
import kf.primitives.WCompiling
import kf.primitives.WTools.w_dumpCode
import kf.recorder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class WCompilingTest : ForthTestCase() {
    val mod: WCompiling = vm.modulesLoaded["Compiling"]!! as WCompiling

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
        mod.w_doLit(vm)
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

    @Test
    fun w_recursiveIntegration() {
        // normal func
        assertFailsWith<ParseError> { eval(": f1 f1 ;") }
        recorder.clearOutput()

        // normal func w/predecessor
        eval(": f2 42 ; : f2 f2 ; f2")
        assertDStack(42)
        assertPrinted(
            "Skipping currently-defining word because it isn't recursive\n")

        // recursive func
        val c = ": f3 recursive 1- dup dup . 0 > if f3 then ; 3 f3 "
        eval(c)
        assertPrinted("2 1 0 ")
    }

    @Test
    fun w_postpone() {
        eval(": plus1 1+ ;")
        eval(": plus2 immediate postpone plus1 postpone plus1 ;")
        assertDStack()

        // You shouldn't do this --- plus2 is meant to be used in other defs;
        // but it does work
        eval("10 plus2")
        assertDStack(12)
        assertTrue(recorder.output().length > 10)
        recorder.clearOutput()

        eval(": uses-plus2 10 plus2 ;")
        assertDStack()

        eval("uses-plus2")
        assertDStack(12)
        assertTrue(recorder.output().isEmpty())

        eval(": iff immediate postpone if ;")
        eval(": test 10 iff 20 then ; test")
        assertDStack(20)
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

    @Test
    fun w_recursive() {
        eval(": a ; ")
        assertFalse(vm.dict["a"].recursive, )
        eval(": a recursive ; ")
    }

    @Test
    fun w_bracketCompile() {
        TODO()
    }

    @Test
    fun w_compileComma() {
        TODO()
    }
}