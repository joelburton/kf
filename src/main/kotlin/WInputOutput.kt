package kf


class WInputOutput(val vm: ForthVM) : WordClass {
    override val name = "InputOutput"
    override val primitives: Array<Word> = arrayOf<Word>(
        Word("cr") { _ -> w_cr() },
        Word("emit") { _ -> w_emit() },
        Word("space") { _ -> w_space() },
        Word("page") { _ -> w_page() },

        Word("nl") { _ -> w_newline() },
        Word("bl") { _ -> w_blank() },

        Word("key") { _ -> w_key() },  // numbers?

        Word(".") { _ -> w_dot() },
        Word("base") { _ -> w_base() },
        Word("hex") { _ -> w_hex() },
        Word("decimal") { _ -> w_decimal() },
        Word("octal") { _ -> w_octal() },
        Word("binary") { _ -> w_binary() },
        Word("dec.") { _ -> w_decimalDot() },
        Word("hex.") { _ -> w_hexDot() },
        Word(".\"") { _ -> w_printLitString() },
        Word("char") { _ -> w_char() },
        Word(
            "[char]",
            imm = true,
            compO = true
        ) { _ -> w_bracketChar() },
        Word("toupper") { _ -> w_toUpper() },
        Word("tolower") { _ -> w_toLower() },

        // word : get a word, store "somewhere", return addr to
        Word("d.") { _ -> w_dDot() },

        )

    private fun w_dDot() {
        val hi: Int = vm.dstk.pop()
        val lo: Int = vm.dstk.pop()
        val combined = (hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFFL)
        vm.io.output.print(combined.toString(vm.base.coerceIn(2, 36)) + " ")
    }

    private fun w_page() {
        vm.io.clearScreen()
    }

    private fun w_decimalDot() {
        vm.io.output.print(vm.dstk.pop().toString(10) + " ")
    }

    private fun w_hexDot() {
        vm.io.output.print("$" + vm.dstk.pop().toString(16) + " ")
    }


    private fun w_char() {
        val token: String = vm.getToken()
        if (token.length != 1) throw ForthError("Char literal must be one character")
        vm.dstk.push(vm.interpToken!![0].code)
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
        vm.io.output.print(s)
    }


    /**  ( x -- out:"" ) Pop & print top of stack. */
    fun w_dot() {
        vm.io.output.print(
            vm.dstk.pop().toString(vm.base.coerceIn(2, 36)) + " "
        )
    }

    /**  ( -- out:"\n" ) Emit newline. */
    fun w_cr() {
        vm.io.output.println()
    }

    /**  ( n -- out:"char-of-n" ) */
    fun w_emit() {
        val c = vm.dstk.pop()
        vm.dbg("w_emit: " + c.toChar())
        vm.io.output.printf("%c", c)
    }

    /**  ( -- out:" " ) */
    fun w_space() {
        vm.io.output.print(" ")
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