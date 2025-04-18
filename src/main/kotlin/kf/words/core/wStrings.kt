package kf.words.core

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.mem.appendStr
import kf.mem.appendStrToData
import kf.mem.appendWord
import kf.strFromAddrLen

object wStrings : IWordModule {
    override val name = "kf.words.core.wStrings"
    override val description = "Strings"

    override val words
        get() = arrayOf<Word>(
            Word("COUNT", ::w_count),
            Word(".\"", ::w_dotQuote, imm = true),
            Word("S\"", ::w_sQuote, imm = true),
            Word("TYPE", ::w_type),
        )

    /** `COUNT` ( c-addr1 -- c-addr2 u ) First char, length of counted str */

    fun w_count(vm: IForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(addr + 1, vm.mem[addr])
    }

    /** `."` ( -- ) Display string */

     fun w_dotQuote(vm: IForthVM) {
        val s = vm.source.scanner.parse('"').strFromAddrLen(vm)

        if (vm.interp.isInterpreting) {
            vm.io.print(s)
        } else {
            vm.appendStr(s)
            vm.appendWord("TYPE")
        }
    }

    /** `S"` ( -- c-addr u ) Get address and length of string */

    fun w_sQuote(vm: IForthVM) {
            val s = vm.source.scanner.parse('"').strFromAddrLen(vm)

        if (vm.interp.isInterpreting) {
            val strAddr = vm.appendStrToData(s)
            vm.dstk.push(strAddr, s.length)
        } else {
            vm.appendStr(s)
        }
    }

    /** `TYPE` ( c-addr u -- ) Display string */

    fun w_type(vm: IForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val output = (0 until len)
            .map { vm.mem[addr + it].toChar() }
            .joinToString("")
        vm.io.print(output)
    }
}
