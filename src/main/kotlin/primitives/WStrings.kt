package kf.primitives

import kf.CellMeta
import kf.ForthVM
import kf.Word
import kf.WordClass

object WStrings : WordClass {
    override val name = "Strings"
    override val primitives get() = arrayOf(
        Word("type", ::w_type),
        Word("s\"", ::w_sQuote),
        Word("source", ::w_source),
        Word(".\"", ::w_dotQuote, imm = true),
        Word("lit-string", ::w_litString),

    //            new Word("c,", Strings::w_cComma),
    //            new Word("c@", Strings::w_cFetch),
    //            new Word("c!", Strings::w_cStore),
    )


    fun w_type(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val output = (0 until len)
            .map { vm.mem[addr + it].toChar() }
            .joinToString("")
        vm.io.print(output)
    }

    fun w_sQuote(vm: ForthVM) {
        if (vm.isInterpretingState) {
            val (addr, len) = vm.interpScanner.parse('"')
            val s = vm.interpScanner.getAsString(addr, len)
            val strAddr: Int = vm.appendStrToData(s)
            vm.dstk.push(strAddr)
            vm.dstk.push(s.length)
        } else {
            val (addr, len) = vm.interpScanner.parse('"')
            val s = vm.interpScanner.getAsString(addr, len)
            val strAddr: Int = vm.appendStrToData(s)
            vm.appendWord("lit-string")
            vm.appendCode(strAddr, CellMeta.StringLit)
            vm.appendCode(len, CellMeta.StringLit)
        }
    }

    fun w_source(vm: ForthVM) {
        vm.dstk.push(vm.interpScanner.bufStartAddr)
        vm.dstk.push(vm.interpScanner.bufLen)
    }

    /** `."` `( -- : out:"str" : print string following )` */

    private fun w_dotQuote(vm: ForthVM) {
        if (vm.isInterpretingState) {
            val (addr, len) = vm.interpScanner.parse('"')
            val s = vm.interpScanner.getAsString(addr, len)
            vm.io.print(s)
        } else {
            val (addr, len) = vm.interpScanner.parse('"')
            val s = vm.interpScanner.getAsString(addr, len)
            val strAddr: Int = vm.appendStrToData(s)
            vm.appendWord("lit-string")
            vm.appendCode(strAddr, CellMeta.StringLit)
            vm.appendCode(len, CellMeta.StringLit)
            vm.appendWord("type")
        }
    }

    fun w_litString(vm: ForthVM) {
        vm.dstk.push(vm.mem[vm.ip++])
        vm.dstk.push(vm.mem[vm.ip++])
    }


}