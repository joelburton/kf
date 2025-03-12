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
        vm.io.o.print(output)
    }

    fun w_sQuote() {
        var s: String = vm.interpScanner!!.findInLine(".+?\"")
            ?: throw ForthError("String literal not closed")
        // get rid of leading single space and terminating quote
        s = s.substring(1, s.length - 1)
        val strAddr: Int = vm.appendStrToData(s)
        vm.dstk.push(strAddr)
        vm.dstk.push(s.length)
    }

    // TODO maybe? this is a kludge
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
        var s: String = vm.interpScanner!!.findInLine(".+?\"")
            ?: throw ParseError("String literal not closed")
        // get rid of leading single space and terminating quote
        s = s.substring(1, s.length - 1)
        vm.io.o.print(s)
    }


}