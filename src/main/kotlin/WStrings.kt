package kf

class WStrings(val vm: ForthVM) : WordClass {
    override val name = "Strings"
    override val primitives: Array<Word> = arrayOf(
        Word("type") { w_type() },
        Word("s\"") { w_sQuote()
        }, Word("source") { w_source() },
        Word(".\"") { w_dotQuote() },


        //            new Word("c,", Strings::w_cComma),
        //            new Word("c@", Strings::w_cFetch),
        //            new Word("c!", Strings::w_cStore),
    )


    fun w_type() {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val output = (0 until len)
            .map { vm.mem[addr + it].toChar() }
            .joinToString("")
        vm.io.print(output)
    }

    fun w_sQuote() {
        val (addr, len) = vm.interpScanner.parse('"')
        val s = vm.interpScanner.getAsString(addr, len)
        val strAddr: Int = vm.appendStrToData(s)
        vm.dstk.push(strAddr)
        vm.dstk.push(s.length)
    }

    fun w_source() {
        vm.dstk.push(vm.interpScanner.bufStartAddr)
        vm.dstk.push(vm.interpScanner.bufLen)
    }

    /** `."` `( -- : out:"str" : print string following )` */

    private fun w_dotQuote() {
        val (addr, len) = vm.interpScanner.parse('"')
        val s = vm.interpScanner.getAsString(addr, len)
        vm.io.print(s)
    }


}