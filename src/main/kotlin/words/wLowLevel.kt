package kf.words

import kf.CellMeta
import kf.D
import kf.ForthBrk
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.addr


object wLowLevel : IWordClass {
    override val name = "LowLevel"
    override val description = "Internal words needed for VM"

    override val words = arrayOf<Word>(
        Word("BRK", ::w_brk),
        Word("NOP", ::w_nop),
        Word("LIT", ::w_lit, compO = true),
        Word("0BRANCH-ABS", ::w_0branchAbs),
        Word("BRANCH-ABS", ::w_branchAbs),
        Word("0BRANCH", ::w_0branch),
        Word("BRANCH", ::w_branch),
        Word("DO-LIT", ::w_doLit),
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
        throw ForthBrk("brk at ${vm.ip.addr} (${vm.ip})")
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
        val wn: Int = vm.dict.getNum("lit")
        vm.dstk.push(wn)
    }



}

object wLowLevelDebug : IWordClass {
    override val name = "LowLevel Custom"
    override val description = "Custom internal words needed for VM"

    override val words = arrayOf<Word>(
        Word("(FOO)", ::w_parenFoo),
        Word("(.)", ::w_parenDot),
        Word("(WORDS)", ::w_parenWords)
    )

    fun w_parenWords(vm: ForthVM) {
        for (word in vm.dict.words) {
            print("${word.name} ")
        }
    }
    fun w_parenFoo(vm: ForthVM) {
        vm.io.print("foo")
    }

    fun w_parenDot(vm: ForthVM) {
        vm.io.print("${vm.dstk.pop()} ")
    }
}