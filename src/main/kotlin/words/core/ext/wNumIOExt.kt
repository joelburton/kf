package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wNumIOExt : IWordModule {
    override val name = "kf.words.core.ext.wNumIOExt"
    override val description = "Numerical input/output"
    override val words = arrayOf<Word>(
        Word("HEX", ::w_hex),
        Word("U.R", ::w_dotR),
        Word(".R", ::w_dotR),
    )

    /** `HEX` `( -- : set base to 16 )` */

    fun w_hex(vm: ForthVM) {
        vm.base = 16
    }

    /** `.R` ( n1 n2 -- ) Display n1 right-aligned in a n2-wide field */

    fun w_dotR(vm: ForthVM) {
        val width: Int = vm.dstk.pop()
        val v: Int = vm.dstk.pop()
        vm.io.print(v.toString(vm.base).padStart(width))
    }
}