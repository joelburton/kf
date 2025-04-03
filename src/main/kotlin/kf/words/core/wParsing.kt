package kf.words.core

import kf.mem.CellMeta
import kf.D
import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.strFromAddrLen
import kf.toForthInt

object wParsing : IWordModule {
    override val name = "kf.words.core.wParsing"
    override val description = "Parsing buffers"

    override val words
        get() = arrayOf<Word>(
            Word("WORD", ::w_word),
            Word("SOURCE", ::w_source),
            Word(">IN", ::w_toIn),
            Word(">NUMBER", ::w_toNumber),
        )

    /** WORD ( char "<chars>ccc<char>" -- c-addr ) Parse word from input
     *
     * Skip leading delimiters. Parse characters ccc delimited by char.
     *
     * c-addr is the address of a transient region containing the parsed word
     * as a counted string. If the parse area was empty or contained no
     * characters other than the delimiter, the resulting string has a zero
     * length.
     */

    fun w_word(vm: IForthVM) {
        val char = vm.dstk.pop().toChar()
        val (addr, len) = vm.source.scanner.wordParse(char)

        val bufAddr = vm.memConfig.scratchStart
        var i = bufAddr
        vm.cellMeta[i] = CellMeta.StringLen
        vm.mem[i++] = len
        for (j in addr until addr + len) {
            vm.cellMeta[i] = CellMeta.CharLit
            vm.mem[i++] = vm.mem[j]
        }

        vm.dstk.push(bufAddr)
    }

    /** SOURCE ( -- c-addr u ) Address of # of chars in input buffer */

    fun w_source(vm: IForthVM) {
        vm.dstk.push(vm.source.scanner.start)
        vm.dstk.push(vm.source.scanner.nChars)
    }

    /** >IN ( -- a-addr ) a-addr is addr of cells with bufPtr - bufStart */

    fun w_toIn(vm: IForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_IN_PTR)
    }

    /**
     * >NUMBER  to-number     CORE
     *
     * ( ud1 c-addr1 u1 -- ud2 c-addr2 u2 )
     *
     * ud2 is the unsigned result of converting the characters within the
     * string specified by c-addr1 u1 into digits, using the number in BASE,
     * and adding each into ud1 after multiplying ud1 by the number in BASE.
     * Conversion continues left-to-right until a character that is not
     * convertible, including any "+" or "-", is encountered or the string is
     * entirely converted. c-addr2 is the location of the first unconverted
     * character or the first character past the end of the string if the
     * string was entirely converted. u2 is the number of unconverted
     * characters in the string. An ambiguous condition exists if ud2 overflows
     * during the conversion.
     */

    fun w_toNumber(vm: IForthVM) {
        // This implementation is limited --- it throws an error on failure,
        // rather than returning addr-of-string and len-of-unconverted.

        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        vm.dstk.dblPop()  // we don't actually use these

        val s = Pair(addr, len).strFromAddrLen(vm)
        if (D) vm.dbg(3, "w_toNumber: s=\"$s\"")
        val i = s.toForthInt(vm.base)   // just assume it works for now
        vm.dstk.push(i, 0, /* addr */ 0, /* # unconverted */ 0)
    }
}