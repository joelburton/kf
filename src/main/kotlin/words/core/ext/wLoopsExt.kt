package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wLoopsExt : IWordModule {
    override val name = "kf.words.core.ext.wLoopsExt"
    override val description = "Loops Extension"
    override val words
        get() = arrayOf(
            Word("AGAIN", ::w_again, imm = true, compO = true),
            Word("?DO", ::w_questionDo, imm=true, compO = true),
        )

    /** `AGAIN` IM CO
     *
     * BEGIN ... AGAIN
     *
     * This is immediate; it just finds the address of the start of the loop
     * and adds the jump to there to the definition.
     * */

    fun w_again(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("branch", bwref - vm.cend - 1)
    }

    fun w_questionDo(vm: ForthVM) {
        TODO()
    }
}