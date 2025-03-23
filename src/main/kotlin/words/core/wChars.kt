package kf.words.core

import kf.CharLitError
import kf.ForthVM
import kf.ForthVM.Companion.CHAR_SIZE
import kf.IWordModule
import kf.Word
import kf.strFromAddrLen

object wChars: IWordModule {
    override val name = "kf.words.core.wChars"
    override val description = "Characters & char memory"

    override val words
        get() = arrayOf(
            Word("C!", ::w_cStore),
            Word("C,", ::w_cComma),
            Word("C@", ::w_cFetch),

            Word("CHAR", ::w_char),
            Word("[CHAR]", ::w_bracketChar, imm = true, compO = true),

            Word("CHAR+", ::w_charPlus),
            Word("CHARS", ::w_chars),
        )

    /** `C!` `( char c-addr -- )` Store char at c-addr */

    fun w_cStore(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val char = vm.dstk.pop()
        vm.mem[addr] = char
    }

    /** `C,` `( char -- )` Store  char at next available space in DATA */

    fun w_cComma(vm: ForthVM) {
        val char = vm.dstk.pop()
        vm.mem[vm.dend++] = char
    }

    /** `C@` `( c-addr -- char )` Fetch char stored at c-addr */

    fun w_cFetch(vm: ForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(vm.mem[addr])
    }

    /** `CHAR` `( "<spaces>name" -- char )` Push first letter of name */

    fun w_char(vm: ForthVM) {
        val token =  vm.scanner.parseName().strFromAddrLen(vm)
        if (token.isEmpty()) throw CharLitError("Empty")
        vm.dstk.push(token[0].code)
    }


    /** ```[CHAR]``` IM CO
     *
     * Compilation: ( "<spaces>name" -- )
     *
     *   Skip leading space delimiters. Parse name delimited by a space. Append
     *   the run-time semantics given below to the current definition.
     *
     * Run-time: ( -- char )
     *
     *   Place char, the value of the first character of name, on the stack.
     */

    fun w_bracketChar(vm: ForthVM) {
        val token = vm.scanner.parseName().strFromAddrLen(vm)
        if (token.isEmpty()) throw CharLitError("Empty")
        vm.appendLit(token[0].code)
    }

    /** `CHAR+` `( c-addr1 -- c-addr2 )` Add size of a char to c-addr1 */

    fun w_charPlus(vm: ForthVM) {
        val addr1 = vm.dstk.pop()
        vm.dstk.push(addr1 + CHAR_SIZE)
    }

    /** `CHARS` ( n1 -- n2 ) n2 is the size in address units of n1 chars */

    fun w_chars(vm: ForthVM) {
        val n1 = vm.dstk.pop()
        vm.dstk.push(n1 * CHAR_SIZE)
    }

}