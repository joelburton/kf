package kf.words.core

import com.github.ajalt.mordant.input.KeyboardEvent
import com.github.ajalt.mordant.input.enterRawModeOrNull
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.terminal.prompt
import kf.*

object wIO : IWordModule {
    override val name = "kf.words.core.wIO"
    override val description = "General input and output"

    override val words
        get() = arrayOf(
            Word("CR", ::w_cr),
            Word("SPACE", ::w_space),
            Word("SPACES", ::w_spaces),
            Word("EMIT", ::w_emit),
            Word("ACCEPT", ::w_accept),
            Word("KEY", ::w_key),
            Word("BL", ::w_bl),
        )

    /** `CR` ( -- ) Cause subsequent output to be on next line */

    fun w_cr(vm: ForthVM) {
        vm.io.println()
    }

    /** `SPACE` ( -- ) Display one space */

    fun w_space(vm: ForthVM) {
        vm.io.print(" ")
    }

    /** `SPACES` ( n -- ) Display n spaces */

    fun w_spaces(vm: ForthVM) {
        repeat(vm.dstk.pop()) { vm.io.print(" ") }
    }

    /** `EMIT` ( x -- ) Display character with value x */

    fun w_emit(vm: ForthVM) {
        vm.io.print(vm.dstk.pop().toChar())
    }

    /** `DECIMAL` ( -- ) Set the numeric conversion radix to ten (decimal) */

    fun w_decimal(vm: ForthVM) {
        vm.base = 10
    }

    /** BASE ( -- a-addr ) a-addr is the address of base */

    fun w_base(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_BASE)
    }

    /** ACCEPT ( c-addr +n1 -- +n2 ) Get line to c-addr (n1 max len, n2 len) */

    fun w_accept(vm: ForthVM) {
        val maxLen = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val s = vm.source.readLineOrNull()
        if (s == null) {
            // fixme: what to do?
            return
        }
        if (s.length > maxLen)
            throw ForthBufferError("Input exceeded max length: $maxLen")
        for (i in s.indices) vm.mem[addr + i] = s[i].code
        vm.dstk.push(s.length)
    }

    /** KEY ( -- char ) Receive one character (not displayed) */

    fun w_key(vm: ForthVM) {
        // todo: much of this should probably move down into the IO layer

        val ti = vm.io.terminalInterface
        if (ti is TerminalFileInterface || ti is TerminalTestInterface)
            throw ForthIOError("Cannot use `key` from file input")

        val rawMode = vm.io.enterRawModeOrNull()
        if (rawMode == null) {
            var s = vm.io.prompt(yellow("Enter 1 character"))
            while (s == null || s.length != 1) {
                s = vm.io.prompt(yellow("Try again, enter 1 character"))
            }
            vm.dstk.push(s[0].code)
        } else {
            rawMode.use {
                var k: Char = rawMode.run {
                    var keyEv: KeyboardEvent? = null
                    while (keyEv == null) keyEv = rawMode.readKeyOrNull()
                    keyEv.key[0].toChar()
                }
                vm.dstk.push(k.code)
            }
        }
    }

    /** BL  b-l   CORE
     *
     * ( -- char )
     *
     * char is the character value for a space.
     */

    fun w_bl(vm: ForthVM) {
        vm.dstk.push(0x20)
    }

}