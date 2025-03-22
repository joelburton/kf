package kf.words.core.ext

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wCommentsExt : IWordClass {
    override val name = "core.ext.commentsExt"
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