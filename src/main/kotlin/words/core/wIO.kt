package kf.words.core

import com.github.ajalt.mordant.input.KeyboardEvent
import com.github.ajalt.mordant.input.enterRawModeOrNull
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.terminal.prompt
import kf.ForthVM
import kf.IWordClass
import kf.TerminalFileInterface
import kf.TerminalTestInterface
import kf.Word
import kf.w_notImpl
import kotlin.use

object wIO : IWordClass {
    override val name = "IO"
    override val description = "General input and output"

    override val words
        get() = arrayOf(
            Word("CR", ::w_cr),
            Word("SPACE", ::w_space),
            Word("SPACES", ::w_spaces),
            Word("EMIT", ::w_emit),
            Word("ACCEPT", ::w_notImpl),
            Word("KEY", ::w_key),
            Word("BL", ::w_bl),
        )

    /** cr   c-r     CORE
     *
     * ( -- )
     *
     * Cause subsequent output to appear at the beginning of the next line.
     */

    fun w_cr(vm: ForthVM) {
        vm.io.println()
    }

    /** SPACE    CORE
     *
     * ( -- )
     *
     * Display one space.
     */

    fun w_space(vm: ForthVM) {
        vm.io.print(" ")
    }

    /** SPACES   CORE
     *
     * ( n -- )
     *
     * If n is greater than zero, display n spaces.
     */

    fun w_spaces(vm: ForthVM) {
        val n = vm.dstk.pop()
        repeat(n) { vm.io.print(" ") }
    }

    /** EMIT     CORE
     *
     * ( x -- )
     *
     * If x is a graphic character in the implementation-defined character set
     * , display x. The effect of EMIT for all other values of x is
     * implementation-defined.
     */

    fun w_emit(vm: ForthVM) {
        val c = vm.dstk.pop()
        vm.io.print(c.toChar().toString())
    }

    /** DECIMAL  CORE
     *
     * ( -- )
     *
     * Set the numeric conversion radix to ten (decimal).
     */

    fun w_decimal(vm: ForthVM) {
        vm.base = 10
    }

    /** BASE     CORE
     *
     * ( -- a-addr )
     *
     * a-addr is the address of a cell containing the current number-conversion
     * radix {{2...36}}.
     */

    fun w_base(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_BASE)
    }

    /** ACCEPT   CORE
     *
     * ( c-addr +n1 -- +n2 )
     *
     * Receive a string of at most +n1 characters. An ambiguous condition
     * exists if +n1 is zero or greater than 32,767. Display graphic characters
     * as they are received. A program that depends on the presence or absence
     * of non-graphic characters in the string has an environmental dependency.
     * The editing functions, if any, that the system performs in order to
     * construct the string are implementation-defined.
     *
     * Input terminates when an implementation-defined line terminator is
     * received. When input terminates, nothing is appended to the string, and
     * the display is maintained in an implementation-defined way.
     *
     * +n2 is the length of the string stored at c-addr.
     */


    /** KEY  CORE
     *
     * ( -- char )
     *
     *  Receive one character char, a member of the implementation-defined
     *  character set. Keyboard events that do not correspond to such
     *  characters are discarded until a valid character is received, and those
     *  events are subsequently unavailable.
     *
     * All standard characters can be received. Characters received by KEY are
     * not displayed.
     */

    fun w_key(vm: ForthVM) {
        // todo: much of this should probably move down into the IO layer
        val ti = vm.io.terminalInterface
        if (ti is TerminalFileInterface || ti is TerminalTestInterface)
            throw RuntimeException("Cannot use `key` from file input")

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