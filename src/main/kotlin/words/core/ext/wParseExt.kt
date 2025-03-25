package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wParseExt: IWordModule {
    override val name = "kf.words.core.ext.wParseExt"
    override val description = "Input/Output"

    override val words = arrayOf<Word>(
        Word("PARSE", ::w_parse)
    )

    /** `PARSE` ( char "ccc<char>" -- c-addr u ) */
    fun w_parse(vm: ForthVM) {
        val char = vm.dstk.pop()
        val (addr, len) = vm.source.scanner.parse(char.toChar())
        vm.dstk.push(addr, len)
    }
}