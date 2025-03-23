package kf.words.custom

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.words.core.wFunctions.w_call

object wCreateCustom : IWordModule {
    override val name = "kf.words.custom.wCreateCustom"
    override val description = "Custom words for creating data"

    override val words
        get() = arrayOf(
            Word("ADDR", ::w_addr),
            Word("ADDRCALL", ::w_addrCall, compO = true),
        )


    /**  Used for pure-data things, like "create age 1 allot" (ie variable) */

    fun w_addr(vm: ForthVM) {
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)
    }

    /**`addrcall` ( -- addr : w/currWord: push dpos, then call it )'
     *
     * Used for all "does" words (it's the "function" for a constant). This
     * word will need to have both a dpos and a cpos, and only words
     * made by create + does will have that.
     * */

    fun w_addrCall(vm: ForthVM) {
        w_addr(vm)
        w_call(vm)
    }


}
