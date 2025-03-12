package kf


class WInputOutput(val vm: ForthVM) : WordClass {
    override val name = "InputOutput"
    override val primitives: Array<Word> = arrayOf(
        Word("cr") { w_cr() },
        Word("emit") { w_emit() },
        Word("space") { w_space() },
        Word("page") { w_page() },

        Word("nl") { w_newline() },
        Word("bl") { w_blank() },

        Word("key") { w_key() },  // numbers?

        Word(".") { w_dot() },
        Word("base") { w_base() },
        Word("hex") { w_hex() },
        Word("decimal") { w_decimal() },
        Word("octal") { w_octal() },
        Word("binary") { w_binary() },
        Word("dec.") { w_decimalDot() },
        Word("hex.") { w_hexDot() },
        Word(".\"") { w_printLitString() },
        Word("char") { w_char() },
        Word("[char]", imm = true, compO = true) { w_bracketChar() },
        Word("toupper") { w_toUpper() },
        Word("tolower") { w_toLower() },

        // word : get a word, store "somewhere", return addr to
        Word("d.") { w_dDot() },

        )

    private fun w_dDot() {
        val hi: Int = vm.dstk.pop()
        val lo: Int = vm.dstk.pop()
        val combined = (hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFFL)
        vm.io.o.print(combined.toString(vm.base.coerceIn(2, 36)) + " ")
    }

    private fun w_page() {
        vm.io.clearScreen()
    }

    private fun w_decimalDot() {
        vm.io.o.print(vm.dstk.pop().toString(10) + " ")
    }

    private fun w_hexDot() {
        vm.io.o.print("$" + vm.dstk.pop().toString(16) + " ")
    }


    private fun w_char() {
        val token: String = vm.getToken()
        if (token.length != 1) throw ForthError("Char literal must be one character")
        vm.dstk.push(token[0].code)
    }

    // immediate-mode version of char, writing to code
    private fun w_bracketChar() {
        val token: String = vm.getToken()
        if (token.length != 1) throw ParseError("Char literal must be one character")
        vm.appendLit(token[0].code)
    }

    private fun w_printLitString() {
        var s: String = vm.interpScanner!!.findInLine(".+?\"")
            ?: throw ParseError("String literal not closed")
        // get rid of leading single space and terminating quote
        s = s.substring(1, s.length - 1)
        vm.io.o.print(s)
    }


    /**  ( x -- out:"" ) Pop & print top of stack. */
    fun w_dot() {
        vm.io.o.print(
            vm.dstk.pop().toString(vm.base.coerceIn(2, 36)) + " "
        )
    }

    /**  ( -- out:"\n" ) Emit newline. */
    fun w_cr() {
        vm.io.o.println()
    }

    /**  ( n -- out:"char-of-n" ) */
    fun w_emit() {
        val c = vm.dstk.pop()
        vm.io.o.printf("%c", c)
    }

    /**  ( -- out:" " ) */
    fun w_space() {
        vm.io.o.print(" ")
    }

    fun w_newline() {
        vm.dstk.push(0x0a)
    }

    fun w_blank() {
        vm.dstk.push(0x20)
    }

    fun w_key() {
//        int c = vm.io.safeRead();  FIXME
//        vm.dstk.push(c);
    }

    fun w_base() {
        vm.dstk.push(ForthVM.REG_BASE)
    }

    fun w_hex() {
        vm.base = 16
    }

    fun w_decimal() {
        vm.base = 10
    }

    fun w_binary() {
        vm.base = 2
    }

    fun w_octal() {
        vm.base = 8
    }

    fun w_toUpper() {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().uppercaseChar().code)
    }

    fun w_toLower() {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().lowercaseChar().code)
    }
}