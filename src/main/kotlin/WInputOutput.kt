package kf



class WInputOutput(val vm: ForthVM) : WordClass {
    override val name = "InputOutput"
    override val primitives: Array<Word> = arrayOf(
        Word("cr") { w_cr() },
        Word("emit") { w_emit() },
        Word("space") { w_space() },
        Word("page") { w_page() },

        Word("nl") { w_nl() },
        Word("bl") { w_bl() },

        Word("key") { w_key() },  // numbers?

        Word(".") { w_dot() },
        Word("base") { w_base() },
        Word("hex") { w_hex() },
        Word("decimal") { w_decimal() },
        Word("octal") { w_octal() },
        Word("binary") { w_binary() },
        Word("dec.") { w_decDot() },
        Word("hex.") { w_hexDot() },

        Word("char") { w_char() },
        Word("[char]", imm = true, compO = true) { w_bracketChar() },
        Word("toupper") { w_toUpper() },
        Word("tolower") { w_toLower() },

        // TODO:
        // word : get a word, store "somewhere", return addr to
        //   or parse? should be in interpreter

        )

    /** `cr` `( -- out:"\n": print newline )` */

    fun w_cr() {
        vm.io.o.println()
    }

    /** `emit` `( n -- out:"char-of-n" )` */

    fun w_emit() {
        val c = vm.dstk.pop()
        vm.io.o.printf("%c", c)
    }

    /** `space` `( -- out:" " : print space )` */

    fun w_space() {
        vm.io.o.print(" ")
    }

    /** `page` `( -- : clear screen )` */

    private fun w_page() {
        vm.io.clearScreen()
    }


    // *************************************************************************


    /** `nl` ( -- nlChar : return newline char )` */

    fun w_nl() {
        vm.dstk.push(0x0a)
    }

    /** `bl` ( -- blChar : return space char )` */

    fun w_bl() {
        vm.dstk.push(0x20)
    }


    // *************************************************************************


    /** `key` ( -- char : get a single keystroke )` */

    fun w_key() {
        TODO("Can't implement properly unless we have a cooked term")
    }


    // *************************************************************************


    /** `.`  `( x -- out:"" : pop & print top of stack )` */

    fun w_dot() {
        vm.io.o.print("${vm.dstk.pop().numToStr(vm.base)} ")
    }

    /** `base` `( -- addr : get address of register base )`
     *
     *  There is a kf-specific alias for this in the Registers words, but `base`
     *  is a standard part of Forth, so putting this here, too.
     *
     * */

    fun w_base() {
        vm.dstk.push(ForthVM.REG_BASE)
    }

    /** `hex` `( -- : set base to 16 )` */

    fun w_hex() {
        vm.base = 16
    }

    /** `decimal` `( -- : set base to 10 )` */

    fun w_decimal() {
        vm.base = 10
    }

    /** `binary` `( -- : set base to 2 )` */

    fun w_binary() {
        vm.base = 2
    }

    /** `octal` `( -- : set base to 8 )` */

    fun w_octal() {
        vm.base = 8
    }

    /** `dec.` `( n -- out:"n" : print n in decimal, regardless of base )` */

    private fun w_decDot() {
        vm.io.o.print(vm.dstk.pop().toString(10) + " ")
    }

    /** `hex.` `( n -- out:"hex(n)" : print n in hex, regardless of base )` */

    private fun w_hexDot() {
        vm.io.o.print("$" + vm.dstk.pop().toString(16) + " ")
    }


    // *************************************************************************


    /** `char` `( in:char -- char : get literal value of char )` */

    private fun w_char() {
        val token: String = vm.getToken()
        if (token.length != 1)
            throw ParseError("Char literal must be one character")
        vm.dstk.push(token[0].code)
    }

    /** ```[char]``` `( in:char -- char : get literal value of char )`
     *
     * This is the same as `char`, but is imm mode.
     * */

    private fun w_bracketChar() {
        val token: String = vm.getToken()
        if (token.length != 1)
            throw ParseError("Char literal must be one character")
        vm.appendLit(token[0].code)
    }

    /** `toupper` `( n : n1 : convert n to uppercase if lowercase char )` */

    fun w_toUpper() {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().uppercaseChar().code)
    }

    /** `tolower` `( n : n1 : convert n to lowercase if uppercase char )` */

    fun w_toLower() {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().lowercaseChar().code)
    }
}