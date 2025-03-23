package kf.words.custom

import kf.ForthVM
import kf.IWordModule
import kf.Word


object wTimeCustom : IWordModule {
    override val name = "kf.words.custom.wTimeCustom"
    override val description = "Time Extra words"

    override val words get() = arrayOf(
        Word("MILLIS", ::w_millis),
    )


    /** `millis` ( -- n : number of milliseconds elapsed since VM started. )
     */
    fun w_millis(vm: ForthVM) {
        val millis = vm.timeMarkCreated.elapsedNow().inWholeMilliseconds.toInt()
        vm.dstk.push(millis)
    }
}