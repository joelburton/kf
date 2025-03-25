package kf.words.machine

import kf.*


object wMachine : IWordModule {
    override val name = "kf.words.machine.wMachine"
    override val description = "Internal words needed for VM"

    override val words = arrayOf<Word>(
        Word("BRK", ::w_brk),
        Word("BRK-IMM", ::w_brk, imm = true),
        Word("NOP", ::w_nop),
        Word("LIT", ::w_lit, compO = true),
        Word("0BRANCH-ABS", ::w_0branchAbs),
        Word("BRANCH-ABS", ::w_branchAbs),
        Word("0BRANCH", ::w_0branch),
        Word("BRANCH", ::w_branch),
        Word("DO-LIT", ::w_doLit),
        Word("[LITERAL]", ::w_bracketLiteral, compO = true),
        Word("COLD", ::w_cold, imm = true, interpO = true),
        Word("COLD-RAW", ::w_coldRaw, imm = true, interpO = true),
        Word("LIT-STRING", ::w_litString, compO = true),
        )

    /** `lit` ( -- n : Push next cell directly onto stack and cptr++ )
     *
     * This what the compiler emits for `: a 65 ;` => `lit 65`. When this code
     * is being run, this functions get the 65 and pushes it onto the stack.
     */
    fun w_lit(vm: ForthVM) {
        val v: Int = vm.mem[vm.ip++]
        vm.dstk.push(v)
    }



    // *************************************************************************

    /** `brk` ( -- : Crashes machine )
     *
     * Make sure this is the first word in the dictionary. It's useful to hit
     * `brk` if the VM starts executing in uninitialized memory.
     */
    fun w_brk(vm: ForthVM) {
        val ipOfBrk = vm.ip - 1
//        val callerOfBrk = vm.dict[vm.mem[ipOfBrk]].name
//        throw IntBrk("$callerOfBrk at ${ipOfBrk.addr} ($ipOfBrk)")
    }

    /** `nop` ( -- : Does nothing )
     *
     * Useful for debugging and also for patching existing word definitions
     * without having to redefine them. Plus, it wastes the computer's time,
     * and that's always fun.
     */
    fun w_nop(vm: ForthVM) {
        if (D) vm.dbg(3, "w_nop reached")
    }

    // *************************************************************** branching
    /** `0branch` ( flag -- in:addr : Jump to addr if flag=0 )
     */
    fun w_0branchAbs(vm: ForthVM) {
        val flag = vm.dstk.pop()
        if (flag == 0) {
            if (D) vm.dbg(3, "w_0branchAbs =0 --> ${vm.ip}}")
            vm.ip = vm.mem[vm.ip]
        } else {
            vm.ip += 1
        }
    }

    /** `branch` ( -- in:addr : Unconditional jump )
     */
    fun w_branchAbs(vm: ForthVM) {
        vm.ip = vm.mem[vm.ip]
    }

    /** `0rel-branch` ( flag -- in:offset : Jump to cptr+offset if flag=0 )
     *
     * This is not a conventional Forth word, but it's useful.
     */
    fun w_0branch(vm: ForthVM) {
        val flag = vm.dstk.pop()
        if (flag == 0) {
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
    fun w_branch(vm: ForthVM) {
        vm.ip = vm.mem[vm.ip] + vm.ip
    }

    /** `dolit` ( -- 'lit : push wn for 'lit' onto stack )
     */
    fun w_doLit(vm: ForthVM) {
        val wn: Int = vm.dict["lit"].wn
        vm.dstk.push(wn)
    }

    /**  `bracketLiteral` C ( n -- : write lit token & n to code area )
     */
    fun w_bracketLiteral(vm: ForthVM) {
        val v: Int = vm.dstk.pop()
        vm.appendLit(v)
    }


    /** `cold` ( -- : Immediately stop VM entirely with exit code 1 )
     *
     * This is a harsh, but conventional Forth word. It's practically the only
     * way to stop the gateway. This should not be caught or prevented.
     */
    fun w_cold(vm: ForthVM) {
        vm.reboot(true)
    }

    /** `cold-raw` ( -- : Reboots machine and load minimal primitives )
     *
     * The only primitives loaded will be these and the ones required for the
     * interpreter itself. The rest would need to be loaded with
     * `include-primitives` (which, fortunately, is provided by the
     * interpreter :-)
     */
    fun w_coldRaw(vm: ForthVM) {
        vm.reboot(false)
    }


    // fixme: needs docs, test
    fun w_litString(vm: ForthVM) {
        val len = vm.mem[vm.ip++]
        val addr = vm.ip
        vm.dstk.push(addr, len)
        vm.ip += len
    }



}

