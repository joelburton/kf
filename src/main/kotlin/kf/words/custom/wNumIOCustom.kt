package kf.words.custom

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wNumIOCustom: IWordModule {
    override val name = "kf.words.custom.wNumIOCustom"
    override val description = "Custom words for numbers and IO"
    override val words = arrayOf<Word>(
        Word("OCTAL",::w_octal),
        Word("BINARY",::w_binary),
        Word("DEC.",::w_decDot),
        Word("HEX.",::w_hexDot),
        Word("BIN.",::w_binDot),
    )



    /** `binary` `( -- : set base to 2 )` */

    fun w_binary(vm: IForthVM) {
        vm.base = 2
    }

    /** `octal` `( -- : set base to 8 )` */

    fun w_octal(vm: IForthVM) {
        vm.base = 8
    }

    /** `dec.` `( n -- out:"n" : print n in decimal, regardless of base )` */

    private fun w_decDot(vm: IForthVM) {
        vm.io.print(vm.dstk.pop().toString(10) + " ")
    }

    /** `hex.` `( n -- out:"hex(n)" : print n in hex, regardless of base )` */

    private fun w_hexDot(vm: IForthVM) {
        vm.io.print("$" + vm.dstk.pop().toString(16) + " ")
    }

    /** `bin.` `( n -- out:"hex(n)" : print n in hex, regardless of base )` */

    private fun w_binDot(vm: IForthVM) {
        vm.io.print("%" + vm.dstk.pop().toString(2) + " ")
    }
}