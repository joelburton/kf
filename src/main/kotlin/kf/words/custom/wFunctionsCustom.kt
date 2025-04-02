package kf.words.custom

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord

object wFunctionsCustom : IWordModule {
    override val name = "kf.words.custom.wFunctionsCustom"
    override val description = "Custom words for functions"

    override val words get() = arrayOf<IWord>(
        Word("CALL-BY-ADDR", ::w_callByAddr),
        )

    /**  `call-by-addr` ( addr -- : call addr )
     */
    fun w_callByAddr(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        vm.rstk.push(vm.ip)
        vm.ip = addr
    }
}