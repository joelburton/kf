package interps

import ForthTestCase
import kf.interps.FScanner
import kf.strFromCSAddr
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FScannerTest : ForthTestCase() {
    val start = vm.memConfig.interpBufStart
    val end = vm.memConfig.interpBufEnd

    @Test
    fun reset() {
        val sc = FScanner(vm)
        Assertions.assertEquals(0, vm.inPtr)
        Assertions.assertEquals(0, sc.tokIdx)
        Assertions.assertEquals(0, sc.nChars)
        Assertions.assertEquals(0, sc.tokLen)
        Assertions.assertEquals("", sc.toString())
    }

    @Test
    fun fill() {
        val sc = FScanner(vm)
        sc.fill("ABCDE")
        Assertions.assertEquals(0, vm.inPtr)
        Assertions.assertEquals(0, sc.tokIdx)
        Assertions.assertEquals(5, sc.nChars)
        Assertions.assertEquals(0, sc.tokLen)
        Assertions.assertEquals("ABCDE", sc.toString())
    }

    @Test
    fun test_toString() {
        val sc = FScanner(vm)
        sc.fill("ABCDE")
        Assertions.assertEquals("ABCDE", sc.toString())
    }

    @Test
    fun parseName() {
        val sc = FScanner(vm)
        sc.fill("  ABC  ")
        val (addr, len) = sc.parseName()
        Assertions.assertEquals(start + 2, addr)
        Assertions.assertEquals(3, len)
        Assertions.assertEquals(6, vm.inPtr)

        sc.fill("  ABC")
        val (addr2, len2) = sc.parseName()
        Assertions.assertEquals(start + 2, addr2)
        Assertions.assertEquals(3, len2)
        Assertions.assertEquals(6, vm.inPtr)

        sc.fill("ABC")
        val (addr3, len3) = sc.parseName()
        Assertions.assertEquals(start, addr3)
        Assertions.assertEquals(3, len3)
        Assertions.assertEquals(4, vm.inPtr)

        sc.fill("  ")
        val (addr4, len4) = sc.parseName()
        Assertions.assertEquals(start + 2, addr4)
        Assertions.assertEquals(0, len4)
        Assertions.assertEquals(3, vm.inPtr)

        sc.fill("")
        val (addr5, len5) = sc.parseName()
        Assertions.assertEquals(start, addr5)
        Assertions.assertEquals(0, len5)
        Assertions.assertEquals(1, vm.inPtr)
    }

    @Test
    fun parse() {
        val sc = FScanner(vm)
        sc.fill("ABCx ")
        val (addr, len) = sc.parse('x')
        Assertions.assertEquals(start, addr)
        Assertions.assertEquals(3, len)
        Assertions.assertEquals(4, vm.inPtr)

        sc.fill("  ABCx")
        val (addr2, len2) = sc.parse('x')
        Assertions.assertEquals(start, addr2)
        Assertions.assertEquals(5, len2)
        Assertions.assertEquals(6, vm.inPtr)

        sc.fill("ABC")
        val (addr3, len3) = sc.parse('x')
        Assertions.assertEquals(start, addr3)
        Assertions.assertEquals(3, len3)
        Assertions.assertEquals(4, vm.inPtr)

        sc.fill("  ")
        val (addr4, len4) = sc.parse('x')
        Assertions.assertEquals(start, addr4)
        Assertions.assertEquals(2, len4)
        Assertions.assertEquals(3, vm.inPtr)

        sc.fill("")
        val (addr5, len5) = sc.parse('x')
        Assertions.assertEquals(start, addr5)
        Assertions.assertEquals(0, len5)
        Assertions.assertEquals(1, vm.inPtr)
    }

    @Test
    fun wordParse() {
        val sc = FScanner(vm)
        sc.fill("ABCx ")
        val (addr, len) = sc.wordParse('x')
        Assertions.assertEquals(start, addr)
        Assertions.assertEquals(3, len)
        Assertions.assertEquals(4, vm.inPtr)

        sc.fill("  ABCx")
        val (addr2, len2) = sc.wordParse('x')
        Assertions.assertEquals(start, addr2)
        Assertions.assertEquals(5, len2)
        Assertions.assertEquals(6, vm.inPtr)

        sc.fill("ABC")
        val (addr3, len3) = sc.wordParse('x')
        Assertions.assertEquals(start, addr3)
        Assertions.assertEquals(3, len3)
        Assertions.assertEquals(4, vm.inPtr)

        sc.fill("  ")
        val (addr4, len4) = sc.wordParse('x')
        Assertions.assertEquals(start, addr4)
        Assertions.assertEquals(2, len4)
        Assertions.assertEquals(3, vm.inPtr)

        sc.fill("")
        val (addr5, len5) = sc.wordParse('x')
        Assertions.assertEquals(start, addr5)
        Assertions.assertEquals(0, len5)
        Assertions.assertEquals(1, vm.inPtr)

        // it skips the term character at the start, unlike other parsers
        sc.fill("xxABCx")
        val (addr6, len6) = sc.wordParse('x')
        Assertions.assertEquals(start + 2, addr6)
        Assertions.assertEquals(3, len6)
        Assertions.assertEquals(6, vm.inPtr)

        // it skips the term character at the start, unlike other parsers
        sc.fill("xxABC")
        val (addr7, len7) = sc.wordParse('x')
        Assertions.assertEquals(start + 2, addr7)
        Assertions.assertEquals(3, len7)
        Assertions.assertEquals(6, vm.inPtr)

        // if the term is a space, all whitespace are terms
        sc.fill(" \t ABC ")
        val (addr8, len8) = sc.wordParse(' ')
        Assertions.assertEquals(start + 3, addr8)
        Assertions.assertEquals(3, len8)
        Assertions.assertEquals(7, vm.inPtr)
    }

    @Test
    fun curToken() {
        val sc = FScanner(vm)
        sc.fill("ABC")
        sc.parseName()
        Assertions.assertEquals("ABC", sc.curToken())
    }

    @Test
    fun nextLine() {
        val sc = FScanner(vm)
        sc.fill("ABC")
        sc.nextLine()
        val (_, len) = sc.parseName()
        Assertions.assertEquals(0, len)
    }

    @Test
    fun strFromCSAddr() {
        vm.mem[0x100] = 3
        vm.mem[0x101] = 65
        vm.mem[0x102] = 66
        vm.mem[0x103] = 67
        Assertions.assertEquals("ABC", 0x100.strFromCSAddr(vm))
    }
}