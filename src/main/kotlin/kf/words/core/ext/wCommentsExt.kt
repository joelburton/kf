package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord

object wCommentsExt : IWordModule {
    override val name = "kf.words.core.ext.wCommentsExt"
    override val description = "Comments Extension"

    override val words: Array<IWord> get() = arrayOf(
        Word("\\", imm = true, fn = ::w_backslashComment),
    )

    /**  ( -- : handles backslash comments )
     */
    fun w_backslashComment(vm: ForthVM) {
        vm.source.scanner.nextLine()
    }
}