package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wIOExt: IWordModule {
    override val name = "kf.words.core.ext.wIOExt"
    override val description = "Input/Output"

    override val words = arrayOf<Word>(
        Word("SOURCE-ID", ::w_sourceId),
        Word("RESTORE-INPUT", ::w_notImpl),
        Word("SAVE-INPUT", ::w_notImpl),
    )

    /** `SOURCE-ID` ( -- 0 | -1 | fileid ) Return id of input source
     *
     * -1 : string (EVALUATE)
     *  0 : stdin
     *  # : # of file ID
     *
     */

    fun w_sourceId(vm: ForthVM) {
        vm.dstk.push(vm.sourceId)
    }

    fun w_restoreInput(vm: ForthVM) {
        TODO()
    }

    fun w_saveInput(vm: ForthVM) {
        TODO()
    }
}