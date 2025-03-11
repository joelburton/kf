import kf.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled


import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

fun forthEval(vm: ForthVM, s: String): String {
    (vm.io as IOGateway).resetAndLoadCommands(s)
    try {
        while (true) {
            val wn = vm.mem[vm.ip++]
            vm.currentWord = vm.dict.get(wn)
            vm.currentWord.callable.invoke(vm)
        }
    } catch (e: ForthEOF) {
        return (vm.io as IOGateway).getPrinted()
    }
}

class WCompilingTest {
    val vm = ForthVM(io = IOGateway())
    val comp = WCompiling(vm)

    @BeforeEach
    fun setUp() {
        vm.verbosity = -2
        vm.reboot()
    }

    @Test
    fun w_colon() {
        // integration test
        forthEval(vm, ": test 123 ;")
        assertNotNull(vm.dict.get("test"))
        forthEval(vm, "test")
        assertEquals(1, vm.dstk.size)
        assertEquals(123, vm.dstk.pop())

        assertFailsWith<InvalidState> { forthEval(vm, ": a : test1 123 ;") }
    }

    @Test
    fun w_semicolon() {
        // integration test
        forthEval(vm, ": test 123 ;")
        assertNotNull(vm.dict.get("test"))
        forthEval(vm, "test")
        assertEquals(1, vm.dstk.size)
        assertEquals(123, vm.dstk.pop())

        assertFailsWith<InvalidState> { forthEval(vm, ": test1 123 ; ;") }
    }

    @Test
    fun w_doLit() {
        comp.w_doLit()
        assertEquals(vm.dict.getNum("lit"), vm.dstk.pop())
    }

    @Test
    fun w_bracketTick() {
        assertFailsWith<InvalidState> { forthEval(vm, "['] 123") }
        vm.reset()

        forthEval(vm, ": test ['] dup ; test")
        assertEquals(1, vm.dstk.size)
        assertEquals(vm.dict.getNum("dup"), vm.dstk.pop())
    }

    @Test
    fun w_immediate() {
        // normal func
        forthEval(vm, ": f1 42 ;")
        assertEquals(0, vm.dstk.size)
        forthEval(vm, ": use1 f1 ;")
        assertEquals(0, vm.dstk.size)

        // immediate
        forthEval(vm, ": f2 immediate 42 ;")
        assertEquals(0, vm.dstk.size)
        forthEval(vm, ": use2 f2 42 ;")
        assertEquals(1, vm.dstk.size)
        assertEquals(42, vm.dstk.pop())

        // immediate
        forthEval(vm, ": f3 42 ; immediate")
        assertEquals(0, vm.dstk.size)
        forthEval(vm, ": use3 f3 42 ;")
        assertEquals(1, vm.dstk.size)
        assertEquals(42, vm.dstk.pop())
    }

    @Disabled("is slow, since it nees a stack overflow")
    @Test
    fun w_recursive() {
        // normal func
        assertFailsWith<ParseError> { forthEval(vm, ": f1 f1 ;") }

        // normal func w/predecessor
        forthEval(vm, ": f2 42 ; : f2 f2 ; f2")
        assertEquals(1, vm.dstk.size)
        assertEquals(42, vm.dstk.pop())

        // recursive func
        assertFailsWith<StackOverflow> {
            forthEval(vm, ": f3 recursive 42 f3 ; f3")}
    }

    @Test
    fun w_postpone() {
    }

    @Test
    fun w_bracketLiteral() {
        assertFailsWith<InvalidState> { forthEval(vm, "[literal] 123") }
        vm.reset()

        val initCend = vm.cend
        forthEval(vm, ": test 123 [literal] ;")

        assertEquals(initCend + 4, vm.cend)
        assertEquals(vm.dict.getNum("lit"), vm.mem[initCend + 0])
        assertEquals(123, vm.mem[initCend + 1])
        assertEquals(vm.dict.getNum("[literal]"), vm.mem[initCend + 2])
        assertEquals(vm.dict.getNum("return"), vm.mem[initCend + 3])

        forthEval(vm, ": test2 test ;")
        assertEquals(initCend + 6, vm.cend)
        assertEquals(vm.dict.getNum("test"), vm.mem[initCend + 4])
    }

    // FIXME: perhaps i'm still fuzzy on the point of "literal" ?

    @Test
    fun w_literal() {
        assertFailsWith<InvalidState> { forthEval(vm, "123 literal") }
        vm.reset()

        val initCend = vm.cend
        forthEval(vm, ": test 123 literal ;")
        assertEquals(initCend + 4, vm.cend)
        assertEquals(vm.dict.getNum("[literal]"), vm.mem[initCend])
        assertEquals(vm.dict.getNum("lit"), vm.mem[initCend + 1])
        assertEquals(123, vm.mem[initCend + 2])

        forthEval(vm, ": test2 test ;")
        assertEquals(initCend + 6, vm.cend)
        print(forthEval(vm, "see test2"))
        assertEquals(vm.dict.getNum("test"), vm.mem[initCend])
    }

}