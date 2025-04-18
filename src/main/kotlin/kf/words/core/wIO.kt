package kf.words.core

import kf.*
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM

object wIO : IWordModule {
    override val name = "kf.words.core.wIO"
    override val description = "General input and output"

    override val words
        get() = arrayOf<Word>(
            Word("CR", ::w_cr),
            Word("SPACE", ::w_space),
            Word("SPACES", ::w_spaces),
            Word("EMIT", ::w_emit),
            Word("ACCEPT", ::w_accept),
            Word("KEY", ::w_key),
            Word("BL", ::w_bl),
        )

    /** `CR` ( -- ) Cause subsequent output to be on next line */

    fun w_cr(vm: IForthVM) {
        vm.io.println()
    }

    /** `SPACE` ( -- ) Display one space */

    fun w_space(vm: IForthVM) {
        vm.io.print(" ")
    }

    /** `SPACES` ( n -- ) Display n spaces */

    fun w_spaces(vm: IForthVM) {
        repeat(vm.dstk.pop()) { vm.io.print(" ") }
    }

    /** `EMIT` ( x -- ) Display character with value x */

    fun w_emit(vm: IForthVM) {
        vm.io.print(vm.dstk.pop().toChar().toString())
    }

    /** `DECIMAL` ( -- ) Set the numeric conversion radix to ten (decimal) */

    fun w_decimal(vm: IForthVM) {
        vm.base = 10
    }

    /** BASE ( -- a-addr ) a-addr is the address of base */

    fun w_base(vm: IForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_BASE)
    }

    /** ACCEPT ( c-addr +n1 -- +n2 ) Get line to c-addr (n1 max len, n2 len) */

    fun w_accept(vm: IForthVM) {
        val maxLen = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val s = vm.source.readLineOrNull()
        if (s == null) {
            throw ForthIOError("End of file reached")
        }
        if (s.length > maxLen)
            throw ForthBufferError("Input exceeded max length: $maxLen")
        for (i in s.indices) vm.mem[addr + i] = s[i].code
        vm.dstk.push(s.length)
    }

    /** KEY ( -- char ) Receive one character (not displayed) */

    fun w_key(vm: IForthVM) {
        vm.dstk.push(vm.io.readKey())
    }

    /** BL  b-l   CORE
     *
     * ( -- char )
     *
     * char is the character value for a space.
     */

    fun w_bl(vm: IForthVM) {
        vm.dstk.push(0x20)
    }

}