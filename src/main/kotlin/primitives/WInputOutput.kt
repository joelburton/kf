package kf.primitives

import com.github.ajalt.mordant.input.KeyboardEvent
import com.github.ajalt.mordant.input.enterRawModeOrNull
import com.github.ajalt.mordant.terminal.prompt
import com.github.ajalt.mordant.rendering.TextColors.*
import kf.ForthVM
import kf.ParseError
import kf.TerminalFileInterface
import kf.TerminalTestInterface
import kf.Word
import kf.WordClass
import kf.numToStr


object WInputOutput : WordClass {
    override val name = "InputOutput"
    override val primitives get() = arrayOf(
        Word("page",::w_page),

        Word("nl",::w_nl),

        Word("key?", ::w_keyQuestion),

        Word("hex",::w_hex),
        Word("octal",::w_octal),
        Word("binary",::w_binary),
        Word("dec.",::w_decDot),
        Word("hex.",::w_hexDot),

        Word("toupper",::w_toUpper),
        Word("tolower",::w_toLower),
        Word(".r", ::w_dotR),

        // TODO:
        // word : get a word, store "somewhere", return addr to
        //   or parse? should be in interpreter

    )









    /** `page` `( -- : clear screen )` */

    private fun w_page(vm: ForthVM) {
        vm.io.cursor.move {
            setPosition(0, 0)
            clearScreen()
        }
    }


    // *************************************************************************


    /** `nl` ( -- nlChar : return newline char )` */

    fun w_nl(vm: ForthVM) {
        vm.dstk.push(0x0a)
    }




    // *************************************************************************


    /** `key` ( -- char : get a single keystroke )`
     *
     * In real, interactive terminals, this will wait for a keystroke.
     *
     * */



    // *************************************************************************


    /** `.`  `( x -- out:"" : pop & print top of stack )` */




    /** `hex` `( -- : set base to 16 )` */

    fun w_hex(vm: ForthVM) {
        vm.base = 16
    }



    /** `binary` `( -- : set base to 2 )` */

    fun w_binary(vm: ForthVM) {
        vm.base = 2
    }

    /** `octal` `( -- : set base to 8 )` */

    fun w_octal(vm: ForthVM) {
        vm.base = 8
    }

    /** `dec.` `( n -- out:"n" : print n in decimal, regardless of base )` */

    private fun w_decDot(vm: ForthVM) {
        vm.io.print(vm.dstk.pop().toString(10) + " ")
    }

    /** `hex.` `( n -- out:"hex(n)" : print n in hex, regardless of base )` */

    private fun w_hexDot(vm: ForthVM) {
        vm.io.print("$" + vm.dstk.pop().toString(16) + " ")
    }


    // *************************************************************************






    /** `toupper` `( n : n1 : convert n to uppercase if lowercase char )` */

    fun w_toUpper(vm: ForthVM) {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().uppercaseChar().code)
    }

    /** `tolower` `( n : n1 : convert n to lowercase if uppercase char )` */

    fun w_toLower(vm: ForthVM) {
        val c: Int = vm.dstk.pop()
        vm.dstk.push(c.toChar().lowercaseChar().code)
    }

    fun w_dotR(vm: ForthVM) {
        val width: Int = vm.dstk.pop()
        val v: Int = vm.dstk.pop()
        vm.io.print("${v.toString().padStart(width)} ")
    }

    fun w_keyQuestion(vm: ForthVM) {
        vm.dstk.push(0) // fixme
    }
}