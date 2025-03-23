package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wNumIOExt : IWordModule {
    override val name = "kf.words.core.ext.wNumIOExt"
    override val description = "Numerical input/output"
    override val words = arrayOf<Word>(
        Word("HEX", ::w_hex),
        Word("SOURCE-ID", ::w_notImpl),
        Word("U.R", ::w_notImpl),
        Word(".R", ::w_dotR),
    )

    /** `hex` `( -- : set base to 16 )` */

    fun w_hex(vm: ForthVM) {
        vm.base = 16
    }


    fun w_dotR(vm: ForthVM) {
        val width: Int = vm.dstk.pop()
        val v: Int = vm.dstk.pop()
        vm.io.print("${v.toString().padStart(width)} ")
    }


}