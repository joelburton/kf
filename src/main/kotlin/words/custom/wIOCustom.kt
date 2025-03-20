package kf.words.custom

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wIOCustom: IWordClass {
    override val name = "IOCustom"
    override val description = "Custom words for IO"
    override val words = arrayOf<Word>(
        Word("NL",::w_nl),
    )

    /** `nl` ( -- nlChar : return newline char )` */

    fun w_nl(vm: ForthVM) {
        vm.dstk.push(0x0a)
    }



}