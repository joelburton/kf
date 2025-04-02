package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord
import kf.strFromLenAddr
import kf.words.core.wFormatting

object wFormattingExt: IWordModule {
    override val name = "kf.words.core.ext.wFormattingExt"
    override val description = "Picture words for numbers"

    override val words
        get() = arrayOf<IWord>(
            Word("HOLDS", ::w_holds),
            )

    /** `HOLDS` ( c-addr u -- ) Add string to pictured number */

    fun w_holds(vm: ForthVM) {
        val s = Pair(vm.dstk.pop(), vm.dstk.pop()).strFromLenAddr(vm)
        wFormatting.pict.append(s.reversed())
    }
}