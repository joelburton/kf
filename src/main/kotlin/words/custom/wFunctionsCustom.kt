package kf.words.custom

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wFunctionsCustom : IWordClass {
    override val name = "Functions"
    override val description = "Custom words for functions"

    override val words get() = arrayOf(
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