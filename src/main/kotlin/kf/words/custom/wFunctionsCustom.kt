package kf.words.custom

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wFunctionsCustom : IWordModule {
    override val name = "kf.words.custom.wFunctionsCustom"
    override val description = "Custom words for functions"

    override val words get() = arrayOf<Word>(
        Word("CALL-BY-ADDR", ::w_callByAddr),
        )

    /**  `call-by-addr` ( addr -- : call addr )
     */
    fun w_callByAddr(vm: IForthVM) {
        val addr: Int = vm.dstk.pop()
        vm.rstk.push(vm.ip)
        vm.ip = addr
    }
}