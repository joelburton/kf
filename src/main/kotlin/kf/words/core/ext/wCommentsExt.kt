package kf.words.core.ext

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wCommentsExt : IWordModule {
    override val name = "kf.words.core.ext.wCommentsExt"
    override val description = "Comments Extension"

    override val words: Array<Word> get() = arrayOf(
        Word("\\", imm = true, fn = ::w_backslashComment),
    )

    /**  ( -- : handles backslash comments )
     */
    fun w_backslashComment(vm: IForthVM) {
        vm.source.scanner.nextLine()
    }
}