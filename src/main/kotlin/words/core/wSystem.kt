package kf.words.core

import kf.IWordClass
import kf.Word
import kf.w_notImpl


object wSystem: IWordClass {
    override val name = "System"
    override val description = "The system outside of the VM"

    override val words
        get() = arrayOf(
            Word("ENVIRONMENT?", ::w_notImpl)
        )


}