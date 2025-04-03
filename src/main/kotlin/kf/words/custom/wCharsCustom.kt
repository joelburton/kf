package kf.words.custom

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord

object wCharsCustom: IWordModule {
    override val name = "kf.words.custom.wCharsCustom"
    override val description = "Custom words for characters"
    override val words = arrayOf<IWord>(
        Word("TOUPPER",::w_toUpper),
        Word("TOLOWER",::w_toLower),
        )



    /** `toupper` `( n : n1 : convert n to uppercase if lowercase char )` */

    fun w_toUpper(vm: IForthVM) {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().uppercaseChar().code)
    }

    /** `tolower` `( n : n1 : convert n to lowercase if uppercase char )` */

    fun w_toLower(vm: IForthVM) {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().lowercaseChar().code)
    }

}