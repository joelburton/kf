package kf.words.core

import kf.CellMeta
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.strFromAddrLen

object wStrings: IWordClass {
    override val name = "Strings"
    override val description = "Strings"

    override val words
        get() = arrayOf(
            Word("COUNT", ::w_count),
            Word(".\"", ::w_dotQuote, imm = true),
            Word("S\"", ::w_sQuote, imm = true),
            Word("TYPE", ::w_type),
        )

    /** COUNT    CORE
     *
     * ( c-addr1 -- c-addr2 u )
     *
     * Return the character string specification for the counted string stored
     * at c-addr1. c-addr2 is the address of the first character after c-addr1.
     * u is the contents of the character at c-addr1, which is the length in
     * characters of the string at c-addr2.
     */

    fun w_count(vm: ForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(addr + 1, vm.mem[addr])
    }

    /** ."   dot-quote   CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( "ccc<quote>" -- )
     * Parse ccc delimited by " (double-quote). Append the run-time semantics
     * given below to the current definition.
     *
     * Run-time:
     * ( -- )
     * Display ccc.
     */

    private fun w_dotQuote(vm: ForthVM) {
        if (vm.interp.isInterpreting) {
            val s = vm.scanner.parse('"').strFromAddrLen(vm)
            vm.io.print(s)
        } else {
            val (addr, len) = vm.scanner.parse('"')
            vm.appendWord("lit-string")
            vm.appendCode(len, CellMeta.StringLen)
            for (i in 0 until len) {
                vm.appendCode(vm.mem[addr + i], CellMeta.CharLit)
            }
            vm.appendWord("type")
        }
    }

    /** S"   s-quote     CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( "ccc<quote>" -- )
     * Parse ccc delimited by " (double-quote). Append the run-time semantics
     * given below to the current definition.
     *
     * Run-time:
     * ( -- c-addr u )
     * Return c-addr and u describing a string consisting of the characters
     * ccc. A program shall not alter the returned string.
     */

    fun w_sQuote(vm: ForthVM) {
        if (vm.interp.isInterpreting) {
            val s = vm.scanner.parse('"').strFromAddrLen(vm)
            val strAddr: Int = vm.appendStrToData(s)
            vm.dstk.push(strAddr)
            vm.dstk.push(s.length)
        } else {
            val (addr, len) = vm.scanner.parse('"')
            vm.appendWord("lit-string")
            vm.appendCode(len, CellMeta.StringLen)
            for (i in 0 until len) {
                vm.appendCode(vm.mem[addr + i], CellMeta.CharLit)
            }
        }
    }

    /**
     * TYPE
     * CORE
     * ( c-addr u -- )
     * If u is greater than zero, display the character string specified by
     * c-addr and u.
     */

    fun w_type(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val output = (0 until len)
            .map { vm.mem[addr + it].toChar() }
            .joinToString("")
        vm.io.print(output)
    }





}
