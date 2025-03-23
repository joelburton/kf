package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wIOExt: IWordModule {
    override val name = "kf.words.core.ext.wIOExt"
    override val description = "Input/Output"

    override val words = arrayOf<Word>(
        Word("SOURCE-ID", ::w_notImpl),
        Word("RESTORE-INPUT", ::w_notImpl),
        Word("SAVE-INPUT", ::w_notImpl),
    )

    fun w_sourceId(vm: ForthVM) {
        TODO()
    }

    fun w_restoreInput(vm: ForthVM) {
        TODO()
    }

    fun w_saveInput(vm: ForthVM) {
        TODO()
    }
}