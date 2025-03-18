package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.ParseError
import kf.Word

object wChars: IWordClass {
    override val name = "Chars"
    override val description = "Words dealing with characters & char memory"

    override val words
        get() = arrayOf(
            Word("C!", ::w_cStore),
            Word("C,", ::w_cComma),
            Word("C@", ::w_cFetch),
            Word("CHAR+", ::w_charPlus),
            Word("CHARS", ::w_chars),
            Word("[CHAR]", ::w_bracketChar, imm = true, compO = true),
            Word("CHAR", ::w_char),
        )

    /** C!   c-store     CORE
     *
     * ( char c-addr -- )
     *
     * Store char at c-addr. When character size is smaller than cell size,
     * only the number of low-order bits corresponding to character size are
     * transferred.
     */

    fun w_cStore(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val char = vm.dstk.pop()
        vm.mem[addr] = char
    }

    /** C,  c-comma   CORE
     *
     * ( char -- )
     *
     * Reserve space for one character in the data space and store char in the
     * space. If the data-space pointer is character aligned when C, begins
     * execution, it will remain character aligned when C, finishes execution.
     * An ambiguous condition exists if the data-space pointer is not
     * character-aligned prior to execution of C,.
     */

    fun w_cComma(vm: ForthVM) {
        val char = vm.dstk.pop()
        vm.mem[vm.dend++] = char
    }

    /** C@   c-fetch     CORE
     *
     * ( c-addr -- char )
     *
     * Fetch the character stored at c-addr. When the cell size is greater than
     * character size, the unused high-order bits are all zeroes.
     */

    fun w_cFetch(vm: ForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(vm.mem[addr])
    }

    /** CHAR+    char-plus   CORE
     *
     * ( c-addr1 -- c-addr2 )
     *
     * Add the size in address units of a character to c-addr1, giving c-addr2.
     */

    fun w_charPlus(vm: ForthVM) {
        val addr1 = vm.dstk.pop()
        vm.dstk.push(addr1 + 1)
    }

    /** CHARS    chars   CORE
     *
     * ( n1 -- n2 )
     *
     * n2 is the size in address units of n1 characters.
     */

    fun w_chars(vm: ForthVM) {
        val n1 = vm.dstk.pop()
        vm.dstk.push(n1 * 1)
    }

    /** ```[CHAR]```     bracket-char    CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( "<spaces>name" -- )
     * Skip leading space delimiters. Parse name delimited by a space. Append
     * the run-time semantics given below to the current definition.
     *
     * Run-time:
     * ( -- char )
     * Place char, the value of the first character of name, on the stack.
     */

    private fun w_bracketChar(vm: ForthVM) {
        val token: String = vm.getToken()
        if (token.length != 1)
            throw ParseError("Char literal must be one character")
        vm.appendLit(token[0].code)
    }

    /** CHAR     char    CORE
     *
     * ( "<spaces>name" -- char )
     *
     * Skip leading space delimiters. Parse name delimited by a space. Put the
     * value of its first character onto the stack.
     */

    private fun w_char(vm: ForthVM) {
        val token = vm.getToken()
        if (token.length != 1)
            throw ParseError("Char literal must be one character")
        vm.dstk.push(token[0].code)
    }

}