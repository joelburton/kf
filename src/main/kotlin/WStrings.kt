package kf

class Strings(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        Word("type") { _ ->
            w_type()
        },
        Word("s\"") { _ ->
            w_sQuote(
            )
        },
        Word("source") { _ ->
            w_source(
            )
        },  //            new Word("c,", Strings::w_cComma),
        //            new Word("c@", Strings::w_cFetch),
        //            new Word("c!", Strings::w_cStore),
    )


    fun w_type() {
        val len: Int = vm.dstk.pop()
        val addr: Int = vm.dstk.pop()
        val sb = StringBuilder()
        for (i in 0..<len) {
            sb.append(vm.mem.get(addr + i) as Char)
        }
        vm.io.output.print(sb.toString())
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

    // FIXME maybe? this is a kludge
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
}