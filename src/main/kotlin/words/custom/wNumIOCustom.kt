package kf.words.custom

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wNumIOCustom: IWordClass {
    override val name = "NumIO"
    override val description = "Custom words for numbers and IO"
    override val words = arrayOf<Word>(
        Word("OCTAL",::w_octal),
        Word("BINARY",::w_binary),
        Word("DEC.",::w_decDot),
        Word("HEX.",::w_hexDot),
        Word("BIN.",::w_binDot),
    )



    /** `binary` `( -- : set base to 2 )` */

    fun w_binary(vm: ForthVM) {
        vm.base = 2
    }

    /** `octal` `( -- : set base to 8 )` */

    fun w_octal(vm: ForthVM) {
        vm.base = 8
    }

    /** `dec.` `( n -- out:"n" : print n in decimal, regardless of base )` */

    private fun w_decDot(vm: ForthVM) {
        vm.io.print(vm.dstk.pop().toString(10) + " ")
    }

    /** `hex.` `( n -- out:"hex(n)" : print n in hex, regardless of base )` */

    private fun w_hexDot(vm: ForthVM) {
        vm.io.print("$" + vm.dstk.pop().toString(16) + " ")
    }

    /** `bin.` `( n -- out:"hex(n)" : print n in hex, regardless of base )` */

    private fun w_binDot(vm: ForthVM) {
        vm.io.print("%" + vm.dstk.pop().toString(2) + " ")
    }
}