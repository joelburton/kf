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
    override val primitives: Array<Word> = arrayOf(
        // Keep on top -- this when, if the VM somehow starts running from
        // initialized memory (0), it will try to run word# 0, which will be
        // this, and we'll break.
        Word("brk", ::w_brk),
        Word("nop", ::w_nop),

        // branching
        Word("0branch", ::w_0branch),
        Word("branch", ::w_branch),
        Word("0rel-branch", ::w_0relBranch),
        Word("rel-branch", ::w_relBranch),

        // fundamental machine state
        Word("abort", ::w_abort),
        Word("abort\"", ::w_abortQuote),
        Word("reboot", ::w_reboot, imm = true, interpO = true),
        Word("reboot-raw", ::w_rebootRaw, imm = true, interpO = true),
        Word("bye", ::w_bye),
        Word("cold", ::w_cold, imm = true, interpO = true),
        Word("quit", ::w_quit),

        // fundamental
        Word("lit", ::w_lit, compO = true),  // registers

        // ~~  *terminal*:lineno:char:<2> 20 10

    )

    // *************************************************************************

    /** `brk` ( -- : Crashes machine )
     *
     * Make sure this is the first word in the dictionary. It's useful to hit
     * `brk` if the VM starts executing in uninitialized memory.
     */
    fun w_brk(vm: ForthVM) {
        throw ForthBrk("brk at " + vm.ip)
    }

    /** `nop` ( -- : Does nothing )
     *
     * Useful for debugging and also for patching existing word definitions
     * without having to redefine them. Plus, it wastes the computer's time,
     * and that's always fun.
     */
    fun w_nop(vm: ForthVM) {
    }

    // *************************************************************** branching
    /** `0branch` ( flag -- in:addr : Jump to addr if flag=0 )
     */
    fun w_0branch(vm: ForthVM) {
        val flag = vm.dstk.pop()
        if (flag == 0) {
            if (D) vm.dbg(3, "w_0branch =0 --> ${vm.ip}}")
            vm.ip = vm.mem[vm.ip]
        } else {
            vm.ip += 1
        }
    }

    /** `branch` ( -- in:addr : Unconditional jump )
     */
    fun w_branch(vm: ForthVM) {
        vm.ip = vm.mem[vm.ip]
    }

    /** `0rel-branch` ( flag -- in:offset : Jump to cptr+offset if flag=0 )
     *
     * This is not a conventional Forth word, but it's useful.
     */
    fun w_0relBranch(vm: ForthVM) {
        val flag = vm.dstk.pop()
        if (0 == flag) {
            if (D) vm.dbg(3, "w_0branch =0 --> ${vm.mem[vm.ip].addr}")
            vm.ip = vm.mem[vm.ip] + vm.ip
        } else {
            vm.ip += 1
        }
    }

    /** `rel-branch` ( flag -- in:offset : Unconditional jump to cptr+offset )
     *
     * This is not a conventional Forth word, but it's useful.
     */
    fun w_relBranch(vm: ForthVM) {
        vm.ip = vm.mem[vm.ip] + vm.ip
    }

    // **************************************************** exiting & restarting
    /** `reset` ( -- : Reset machine state {stacks, cptr, etc} )
     */
    fun w_abort(vm: ForthVM) {
        vm.reset()
    }

    /**  `abort"` ( f in:"msg" -- : if flag non-zero, abort w/msg )
     */
    private fun w_abortQuote(vm: ForthVM) {
        val (addr, len) = vm.interpScanner.parse('"')
        val s = vm.interpScanner.getAsString(addr, len)
        val flag: Int = vm.dstk.pop()
        if (flag != 0) {
            vm.io.danger("ABORT: $s")
            vm.reset()
        }
    }

    /** `reboot` ( -- : Reboots machine {clear all mem, stacks, state, etc.} )
     */
    fun w_reboot(vm: ForthVM) {
        vm.reboot(true)
    }

    /** `reboot-raw` ( -- : Reboots machine and load minimal primitives )
     *
     * The only primitives loaded will be these and the ones required for the
     * interpreter itself. The rest would need to be loaded with
     * `include-primitives` (which, fortunately, is provided by the
     * interpreter :-)
     */
    fun w_rebootRaw(vm: ForthVM) {
        vm.reboot(false)
    }

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

    /** `quit` ( -- : Interactive: reset call stack; Non-I: skip all files )
     *
     * This is a harsh, but conventional Forth word. It's practically the only
     * way to stop the gateway. This should not be caught or prevented.
     */
    fun w_quit(vm: ForthVM) {
        throw ForthQuit("Quit")
    }

    // ************************************************************* fundamental

    /** `lit` ( -- n : Push next cell directly onto stack and cptr++ )
     *
     * This what the compiler emits for `: a 65 ;` => `lit 65`. When this code
     * is being run, this functions get the 65 and pushes it onto the stack.
     */
    fun w_lit(vm: ForthVM) {
        val v: Int = vm.mem[vm.ip++]
        vm.dstk.push(v)
    }
}