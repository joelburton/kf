package kf.words

import kf.D
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.toForthInt
import kf.w_notImpl

object wParsing: IWordClass {
    override val name = "Parsing"
    override val description = "Parsing buffers"

    override val words
        get() = arrayOf(
            Word("WORD", ::w_word),
            Word("SOURCE", ::w_source),
            Word(">IN", ::w_notImpl),
            Word(">NUMBER", ::w_toNumber),
        )

    /**
     * WORD     CORE
     *
     * ( char "<chars>ccc<char>" -- c-addr )
     *
     * Skip leading delimiters. Parse characters ccc delimited by char. An
     * ambiguous condition exists if the length of the parsed string is greater
     * than the implementation-defined length of a counted string.
     *
     * c-addr is the address of a transient region containing the parsed word
     * as a counted string. If the parse area was empty or contained no
     * characters other than the delimiter, the resulting string has a zero
     * length. A program may replace characters within the string.
     */

    fun w_word(vm: ForthVM) {
        if (D) vm.dbg(2, "w_parseName")
        val (addr, len) = vm.interp.scanner.parseNameToPAir()
        // fixme: this is ungodly cheating and will break things, but for now:
        // since "WORD" gives back a (yuck) counted string
        vm.mem[addr-1] = len
        vm.dstk.push(addr-1)

    }
    /**
     * SOURCE   CORE
     *
     * ( -- c-addr u )
     *
     * c-addr is the address of, and u is the number of characters in, the
     * input buffer.
     */

    fun w_source(vm: ForthVM) {
        vm.dstk.push(vm.interp.scanner.bufStartAddr)
        vm.dstk.push(vm.interp.scanner.bufLen)
    }

    /**
     * >IN      to-in     CORE
     *
     * ( -- a-addr )
     *
     * a-addr is the address of a cell containing the offset in characters from
     * the start of the input buffer to the start of the parse area.
     */

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

    fun w_toNumber(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val ud1b = vm.dstk.pop()  // we don't actually use these
        val ud1a = vm.dstk.pop()
        val s = vm.interp.scanner.getAsString(addr, len)
        if (D) vm.dbg(2, "w_toNumber: s=\"$s\"")
        val i = s.toForthInt(vm.base)   // just assume it works for now
        vm.dstk.push(i, 0, /* addr */0, /* # unconverted */ 0)
    }
}