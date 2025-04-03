package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.words.core.wCreate.w_create


object wMemoryExt : IWordModule {
    override val name = "kf.words.core.ext.wMemoryExt"
    override val description = "High-level memory"

    override val words: Array<Word>
        get() = arrayOf(
            Word("BUFFER:", ::w_bufferColon),
            Word("PAD", ::w_pad),
            Word("UNUSED", ::w_unused),
            Word("ERASE", ::w_erase),
        )

    /** `BUFFER:` ( u "<spaces>name" -- ) Create & allot u spaces in DATA */
    fun w_bufferColon(vm: IForthVM) {
        w_create(vm)
        vm.dend += vm.dstk.pop()
    }

    /** `PAD` ( -- c-addr ) Return address of PAD */
    fun w_pad(vm: IForthVM) {
        vm.dstk.push(vm.memConfig.padStart)
    }

    /** `ERASE` ( addr u -- ) Erase u cells starting at addr */
    fun w_erase(vm: IForthVM) {
        val len: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        for (i in 0 until len) {
            vm.mem[startAt + i] = 0
        }
    }

    /** `UNUSED` ( -- n ) Return # cells of unused DATA */
    fun w_unused(vm: IForthVM) {
        vm.dstk.push(vm.memConfig.dataEnd - vm.dend)
    }
}