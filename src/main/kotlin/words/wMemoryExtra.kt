package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.w_notImpl
import kf.Word

object wMemoryExtra: IWordClass {
    override val name = "Memory Extra"
    override val description = "Memory management"
     override val words = arrayOf(
        Word(",,", ::w_commaComma),
    )

    fun w_commaComma(vm: ForthVM) {
        vm.mem[vm.cend++] = vm.dstk.pop()
    }

}