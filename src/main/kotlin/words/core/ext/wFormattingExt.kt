package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wFormattingExt: IWordModule {
    override val name = "kf.words.core.ext.wFormattingExt"
    override val description = "Picture words for numbers"

    override val words
        get() = arrayOf(
            Word("HOLDS", ::w_holds),
            )

    fun w_holds(vm: ForthVM) {
        TODO()
    }
}