package kf.words.custom

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord


object wTimeCustom : IWordModule {
    override val name = "kf.words.custom.wTimeCustom"
    override val description = "Time Extra words"

    override val words get() = arrayOf<IWord>(
        Word("MILLIS", ::w_millis),
    )


    /** `millis` ( -- n : number of milliseconds elapsed since VM started. )
     */
    fun w_millis(vm: IForthVM) {
        val millis = vm.timeMarkCreated.elapsedNow().inWholeMilliseconds.toInt()
        vm.dstk.push(millis)
    }
}