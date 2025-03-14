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

    // FIXME: needs to be integrated with new scanner
    // since we keep the lineBuf as a Java string throughout,
    // using source copies it
    // but it should be modifiable, like:
    // 10 65 source drop c! source type => "A0 65 source drop c! source type"
    fun w_source() {
        val s: String = vm.interpLineBuf!!
        val strAddr: Int = vm.appendStrToData(s)
        vm.dstk.push(strAddr)
        vm.dstk.push(s.length)
    }

    /** `."` `( -- : out:"str" : print string following )` */

    private fun w_dotQuote() {
        val (addr, len) = vm.interpScanner.parse('"')
        val s = vm.interpScanner.getAsString(addr, len)
        vm.io.print(s)
    }


}