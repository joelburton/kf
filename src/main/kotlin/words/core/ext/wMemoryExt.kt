package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word


object wMemoryExt : IWordModule {
    override val name = "kf.words.core.ext.wMemoryExt"
    override val description = "High-level memory"

    override val words
        get() = arrayOf(
            Word("BUFFER:", ::w_unused),
            Word("PAD", ::w_unused),
            Word("UNUSED", ::w_unused ) ,
            Word("ERASE", ::w_erase ) ,
        )

    //addr u erase
    private fun w_erase(vm: ForthVM) {
        val len: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = 0
        }
    }

    private fun w_unused(vm: ForthVM) {
        vm.dstk.push(vm.memConfig.dataEnd - vm.dend + 1)
    }
}