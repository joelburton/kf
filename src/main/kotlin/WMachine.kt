package kf

class WMachine(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf(
        // Keep on top -- this when, if the VM somehow starts running from
        // initialized memory (0), it will try to run word# 0, which will be
        // this, and we'll break.
        Word("brk") { _ -> w_brk() },
        Word("nop") { _ -> w_nop() },  // branching

        Word("0branch") { _ -> w_0branch() },
        Word("branch") { _ -> w_branch() },
        Word("0rel-branch") { _ -> w_0relBranch() },
        Word("rel-branch") { _ -> w_relBranch() },

        // fundamental machine state
        Word("abort") { _ -> w_abort() },
        Word("abort\"") { _ -> w_abortQuote() },
        Word("reboot", immediate = true, interpOnly = true,) { _ -> w_reboot() },
        Word("reboot-raw", immediate = true, interpOnly = true) { _ -> w_rebootRaw() },
        Word("bye") { _ -> w_bye() },
        Word("cold", immediate=true, interpOnly = true) { _ -> w_coldStop() },
        Word("quit") { _ -> w_quit() },

        // fundamental
        Word("lit", compileOnly = true) { _ -> w_lit() },  // registers

        Word("term-width") { _ -> w_termWidth() },
        Word("verbosity") { _ -> w_verbosity() },

        // ~~  *terminal*:lineno:char:<2> 20 10

    )


    // TODO:
    // quit
    // Empty return stack, make the user input device the input source,
    // enter interpret state and start the text interpreter.
    // *************************************************************************
    /** `brk` ( -- : Crashes machine )
     *
     * Make sure this is the first word in the dictionary. It's useful to hit
     * `brk` if the VM starts executing in uninitialized memory.
     */
    fun w_brk() {
        if (D) vm.dbg("w_brk")
        throw ForthBrk("brk at " + vm.cptr)
    }

    /** `nop` ( -- : Does nothing )
     *
     * Useful for debugging and also for patching existing word definitions
     * without having to redefine them. Plus, it wastes the computer's time,
     * and that's always fun.
     */
    fun w_nop() {
        if (D) vm.dbg("w_nop")
    }

    // *************************************************************** branching
    /** `0branch` ( flag -- in:addr : Jump to addr if flag=0 )
     */
    fun w_0branch() {
        val flag = vm.dstk.pop()
        if (D) vm.dbg(3, "w_0branch: $flag")
        if (flag == 0) {
            if (D) vm.dbg("w_0branch =0 --> ${vm.cptr}}")
            vm.cptr = vm.mem.get(vm.cptr)
        } else {
            vm.cptr += 1
        }
    }

    /** `branch` ( -- in:addr : Unconditional jump )
     */
    fun w_branch() {
        if (D) vm.dbg(3, "w_branch: --> ${vm.cptr}")
        vm.cptr = vm.mem.get(vm.cptr)
    }

    /** `0rel-branch` ( flag -- in:offset : Jump to cptr+offset if flag=0 )
     *
     * This is not a conventional Forth word, but it's useful.
     */
    fun w_0relBranch() {
        val flag = vm.dstk.pop()
        if (D) vm.dbg(3, "w_0branch: %d %04x", flag, vm.cptr)
        if (0 == flag) {
            if (D) vm.dbg(
                "w_0branch =0 --> %04x",
                vm.mem.get(vm.cptr)
            )
            vm.cptr = vm.mem.get(vm.cptr) + vm.cptr
        } else {
            vm.cptr += 1
        }
    }

    /** `rel-branch` ( flag -- in:offset : Unconditional jump to cptr+offset )
     *
     * This is not a conventional Forth word, but it's useful.
     */
    fun w_relBranch() {
        if (D) vm.dbg(3, "w_rbranch: %04x", vm.cptr)
        vm.cptr = vm.mem.get(vm.cptr) + vm.cptr
    }

    // **************************************************** exiting & restarting
    /** `reset` ( -- : Reset machine state {stacks, cptr, etc} )
     */
    fun w_abort() {
        if (D) vm.dbg("w_abort")
        vm.reset()
    }

    /**  `abort"` ( f in:"msg" -- : if flag non-zero, abort w/msg )
     */
    private fun w_abortQuote() {
        if (D) vm.dbg("w_abortQuote")
        var s: String = vm.interpScanner!!.findInLine(".+?\"")
            ?: throw ForthError("String literal not closed")
        // get rid of leading single space and terminating quote
        s = s.substring(1, s.length - 1)
        val flag: Int = vm.dstk.pop()
        if (flag != 0) {
            vm.io.error("ABORT: $s")
            vm.reset()
        }
    }

    /** `reboot` ( -- : Reboots machine {clear all mem, stacks, state, etc.} )
     */
    fun w_reboot() {
        if (D) vm.dbg("w_reboot")
        vm.reboot(true)
    }

    /** `reboot-raw` ( -- : Reboots machine and load minimal primitives )
     *
     * The only primitives loaded will be these and the ones required for the
     * interpreter itself. The rest would need to be loaded with
     * `include-primitives` (which, fortunately, is provided by the
     * interpreter :-)
     */
    fun w_rebootRaw() {
        if (D) vm.dbg("w_rebootRaw")
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
    fun w_bye() {
        if (D) vm.dbg("w_bye")
        throw ForthBye("Bye!")
    }

    /** `cold` ( -- : Immediately stop VM entirely with exit code 1 )
     *
     * This is a harsh, but conventional Forth word. It's practically the only
     * way to stop the gateway. This should not be caught or prevented.
     */
    fun w_coldStop() {
        if (D) vm.dbg("w_coldStop")
        throw ForthColdStop("1")
    }

    /** `quit` ( -- : Interactive: reset call stack; Non-I: skip all files )
     *
     * This is a harsh, but conventional Forth word. It's practically the only
     * way to stop the gateway. This should not be caught or prevented.
     */
    fun w_quit() {
        if (D) vm.dbg("w_quit")
        throw ForthQuit("Quit")
    }

    // *************************************************************** registers
    fun w_termWidth() {
        if (D) vm.dbg("w_termWidth")
        vm.dstk.push(vm.REG_TERM_WIDTH)
    }

    fun w_verbosity() {
        if (D) vm.dbg("w_verbosity")
        vm.dstk.push(vm.REG_VERBOSITY)
    }

    // ************************************************************* fundamental
    /** `lit` ( -- n : Push next cell directly onto stack and cptr++ )
     *
     * FIXME: is this something like `number` in the standard? */
    fun w_lit() {
        val `val`: Int = vm.mem.get(vm.cptr++)
        if (D) vm.dbg("w_lit %d", `val`)
        vm.dstk.push(`val`)
    }
}