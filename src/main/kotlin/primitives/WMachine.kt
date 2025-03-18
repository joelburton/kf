@file:Suppress("unused")

package kf.primitives

import com.github.ajalt.mordant.terminal.danger
import kf.D
import kf.ForthBrk
import kf.ForthBye
import kf.ForthColdStop
import kf.ForthQuit
import kf.ForthVM
import kf.Word
import kf.WordClass
import kf.addr

object WMachine : WordClass {
    override val name = "Machine"
    override val primitives get() = arrayOf(
        // Keep on top -- this when, if the VM somehow starts running from
        // initialized memory (0), it will try to run word# 0, which will be
        // this, and we'll break.
//        Word("brk", ::w_brk),
//        Word("nop", ::w_nop),

        // branching
//        Word("0branch", ::w_0branch),
//        Word("branch", ::w_branch),
//        Word("0rel-branch", ::w_0relBranch),
//        Word("rel-branch", ::w_relBranch),

        // fundamental machine state
//        Word("abort", ::w_abort),
//        Word("abort\"", ::w_abortQuote),
//        Word("reboot", ::w_reboot, imm = true, interpO = true),
//        Word("reboot-raw", ::w_rebootRaw, imm = true, interpO = true),
        Word("bye", ::w_bye),
        Word("cold", ::w_cold, imm = true, interpO = true),
//        Word("quit", ::w_quit),

        // fundamental
//        Word("lit", ::w_lit, compO = true),  // registers

        // ~~  *terminal*:lineno:char:<2> 20 10

    )


    // **************************************************** exiting & restarting

    /** `bye` ( -- Quit interpreter )
     *
     * Quits the interpreter. If using the interpreter directly, this will end
     * up quitting the entire program. If the interpreter is running an
     * interpreter (like when using `include` to read in additional Forth
     * files), this will quit the inner interpreter and return to the outer
     * one.
     *
     * When running as a long-running gateway to a VM, this is caught and
     * just reboots the machine, starting a new interpreter.
     */
    fun w_bye(vm: ForthVM) {
        throw ForthBye("Bye!")
    }

    /** `cold` ( -- : Immediately stop VM entirely with exit code 1 )
     *
     * This is a harsh, but conventional Forth word. It's practically the only
     * way to stop the gateway. This should not be caught or prevented.
     */
    fun w_cold(vm: ForthVM) {
        throw ForthColdStop("1")
    }



}