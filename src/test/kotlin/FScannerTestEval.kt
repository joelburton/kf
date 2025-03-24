import kf.FScanner
import kf.strFromCSAddr
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FScannerTestEval : ForthTestCase() {
    val start = vm.memConfig.interBufStart
    val end = vm.memConfig.interpBufEnd

    @Test
    fun reset() {
        val sc = FScanner(vm)
        assertEquals(0, vm.inPtr)
        assertEquals(0, sc.tokIdx)
        assertEquals(0, sc.nChars)
        assertEquals(0, sc.tokLen)
        assertEquals("", sc.toString())
    }

    @Test
    fun fill() {
        val sc = FScanner(vm)
        sc.fill("ABCDE")
        assertEquals(0, vm.inPtr)
        assertEquals(0, sc.tokIdx)
        assertEquals(5, sc.nChars)
        assertEquals(0, sc.tokLen)
        assertEquals("ABCDE", sc.toString())
    }

    @Test
    fun test_toString() {
        val sc = FScanner(vm)
        sc.fill("ABCDE")
        assertEquals("ABCDE", sc.toString())
    }

    @Test
    fun parseName() {
        val sc = FScanner(vm)
        sc.fill("  ABC  ")
        val (addr, len) = sc.parseName()
        assertEquals(start + 2, addr)
        assertEquals(3, len)
        assertEquals(6, vm.inPtr)

        sc.fill("  ABC")
        val (addr2, len2) = sc.parseName()
        assertEquals(start + 2, addr2)
        assertEquals(3, len2)
        assertEquals(6, vm.inPtr)

        sc.fill("ABC")
        val (addr3, len3) = sc.parseName()
        assertEquals(start, addr3)
        assertEquals(3, len3)
        assertEquals(4, vm.inPtr)

        sc.fill("  ")
        val (addr4, len4) = sc.parseName()
        assertEquals(start + 2, addr4)
        assertEquals(0, len4)
        assertEquals(3, vm.inPtr)

        sc.fill("")
        val (addr5, len5) = sc.parseName()
        assertEquals(start, addr5)
        assertEquals(0, len5)
        assertEquals(1, vm.inPtr)
    }

    @Test
    fun parse() {
        val sc = FScanner(vm)
        sc.fill("ABCx ")
        val (addr, len) = sc.parse('x')
        assertEquals(start, addr)
        assertEquals(3, len)
        assertEquals(4, vm.inPtr)

        sc.fill("  ABCx")
        val (addr2, len2) = sc.parse('x')
        assertEquals(start, addr2)
        assertEquals(5, len2)
        assertEquals(6, vm.inPtr)

        sc.fill("ABC")
        val (addr3, len3) = sc.parse('x')
        assertEquals(start, addr3)
        assertEquals(3, len3)
        assertEquals(4, vm.inPtr)

        sc.fill("  ")
        val (addr4, len4) = sc.parse('x')
        assertEquals(start, addr4)
        assertEquals(2, len4)
        assertEquals(3, vm.inPtr)

        sc.fill("")
        val (addr5, len5) = sc.parse('x')
        assertEquals(start, addr5)
        assertEquals(0, len5)
        assertEquals(1, vm.inPtr)
    }

    @Test
    fun wordParse() {
        val sc = FScanner(vm)
        sc.fill("ABCx ")
        val (addr, len) = sc.wordParse('x')
        assertEquals(start, addr)
        assertEquals(3, len)
        assertEquals(4, vm.inPtr)

        sc.fill("  ABCx")
        val (addr2, len2) = sc.wordParse('x')
        assertEquals(start, addr2)
        assertEquals(5, len2)
        assertEquals(6, vm.inPtr)

        sc.fill("ABC")
        val (addr3, len3) = sc.wordParse('x')
        assertEquals(start, addr3)
        assertEquals(3, len3)
        assertEquals(4, vm.inPtr)

        sc.fill("  ")
        val (addr4, len4) = sc.wordParse('x')
        assertEquals(start, addr4)
        assertEquals(2, len4)
        assertEquals(3, vm.inPtr)

        sc.fill("")
        val (addr5, len5) = sc.wordParse('x')
        assertEquals(start, addr5)
        assertEquals(0, len5)
        assertEquals(1, vm.inPtr)

        // it skips the term character at the start, unlike other parsers
        sc.fill("xxABCx")
        val (addr6, len6) = sc.wordParse('x')
        assertEquals(start + 2, addr6)
        assertEquals(3, len6)
        assertEquals(6, vm.inPtr)

        // it skips the term character at the start, unlike other parsers
        sc.fill("xxABC")
        val (addr7, len7) = sc.wordParse('x')
        assertEquals(start + 2, addr7)
        assertEquals(3, len7)
        assertEquals(6, vm.inPtr)

        // if the term is a space, all whitespace are terms
        sc.fill(" \t ABC ")
        val (addr8, len8) = sc.wordParse(' ')
        assertEquals(start + 3, addr8)
        assertEquals(3, len8)
        assertEquals(7, vm.inPtr)
    }

    @Test
    fun curToken() {
        val sc = FScanner(vm)
        sc.fill("ABC")
        sc.parseName()
        assertEquals("ABC", sc.curToken())
    }

    @Test
    fun nextLine() {
        val sc = FScanner(vm)
        sc.fill("ABC")
        sc.nextLine()
        val (_, len) = sc.parseName()
        assertEquals(0, len)
    }

    @Test
    fun strFromCSAddr() {
        vm.mem[0x100] = 3
        vm.mem[0x101] = 65
        vm.mem[0x102] = 66
        vm.mem[0x103] = 67
        assertEquals("ABC", 0x100.strFromCSAddr(vm))
    }
}