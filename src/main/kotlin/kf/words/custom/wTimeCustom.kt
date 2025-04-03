package kf.words.custom

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule


object wTimeCustom : IWordModule {
    override val name = "kf.words.custom.wTimeCustom"
    override val description = "Time Extra words"

    override val words get() = arrayOf<Word>(
        Word("MILLIS", ::w_millis),
    )


    /** `millis` ( -- n : number of milliseconds elapsed since VM started. )
     */
    fun w_millis(vm: IForthVM) {
        val millis = vm.timeMarkCreated.elapsedNow().inWholeMilliseconds.toInt()
        vm.dstk.push(millis)
    }
}