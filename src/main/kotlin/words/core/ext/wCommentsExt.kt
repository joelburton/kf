package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wCommentsExt : IWordModule {
    override val name = "kf.words.core.ext.wCommentsExt"
    override val description = "Comments Extension"

    override val words get() = arrayOf(
        Word("\\", imm = true, fn = ::w_backslashComment),
    )

    /**  ( -- : handles backslash comments )
     */
    fun w_backslashComment(vm: ForthVM) {
        vm.scanner.nextLine()
    }
}