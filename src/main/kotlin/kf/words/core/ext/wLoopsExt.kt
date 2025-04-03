package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord
import kf.mem.appendJump

object wLoopsExt : IWordModule {
    override val name = "kf.words.core.ext.wLoopsExt"
    override val description = "Loops Extension"
    override val words: Array<IWord>
        get() = arrayOf(
            Word("AGAIN", ::w_again, imm = true, compO = true),
            Word("?DO", ::w_questionDo, imm=true, compO = true),
            Word("(?DO)", ::w_parenQuestionDo,compO = true, hidden = true)
        )

    /** `AGAIN` IM CO
     *
     * BEGIN ... AGAIN
     *
     * This is immediate; it just finds the address of the start of the loop
     * and adds the jump to there to the definition.
     * */

    fun w_again(vm: IForthVM) {
        val bwref = vm.rstk.pop()
        vm.appendJump("branch", bwref - vm.cend - 1)
    }

    /** `?DO` IM CO (C: -- addr 0 ) Start loop compilation
     *
     * Similar to `DO`, but adds a space for a fwd-ref to end of loop,
     * and emits `(?DO)`, not `(DO)` for runtime.
     * */

    fun w_questionDo(vm: IForthVM) {
        vm.appendJump("(?DO)", 0xffff)
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us
        vm.rstk.push(vm.cend - 1) // push branch loc so loop/+loop can fix
        vm.rstk.push(1)  // how many forward refs we'll need to fix (LEAVE)L
    }

    /** Runtime support for `(?DO)`
     *
     * This is the runtime part. It's called only once, at the start of the
     * first iteration. It skips looping if limit-start; otherwise, it acts
     * just like `(DO)` */

    fun w_parenQuestionDo(vm: IForthVM) {
        val limit = vm.dstk.pop()
        val start = vm.dstk.pop()

        if (start == limit) {
            vm.ip = vm.ip + vm.mem[vm.ip]
        } else {
            vm.rstk.push(limit, start) // limit, start
            vm.ip += 1
        }
    }
}
