package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.dict.w_notImpl
import kf.interfaces.IWord

object wIOExt: IWordModule {
    override val name = "kf.words.core.ext.wIOExt"
    override val description = "Input/Output"

    override val words = arrayOf<IWord>(
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
        vm.dstk.push(vm.source.id)
    }

    fun w_restoreInput(vm: ForthVM) {
        TODO()
    }

    fun w_saveInput(vm: ForthVM) {
        TODO()
    }
}