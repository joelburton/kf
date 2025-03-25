package kf.words.core.ext

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.strFromAddrLen

object wStringsExt: IWordModule {
    override val name = "kf.words.core.ext.wStringsExt"
    override val description = "Strings"

    override val words
        get() = arrayOf(
            Word("C\"", ::w_cQuote),
            Word("S\\\"", ::w_sBackSlashQuote),
        )

    /** `C"` (in:ccc" -- cs-addr ) */
    fun w_cQuote(vm: ForthVM) {
        val s = vm.source.scanner.parse('"').strFromAddrLen(vm)

        if (vm.interp.isInterpreting) {
            val strAddr = vm.appendCStrToData(s)
            vm.dstk.push(strAddr)
        } else {
            vm.appendCStr(s)
        }
    }

    /** `S\"` ( in:ccc" -- c-addr u ) Translate and store string
     *
     * \a	BEL	(alert,	ASCII 7)
     * \b	BS	(backspace,	ASCII 8)
     * \e	ESC	(escape,	ASCII 27)
     * \f	FF	(form feed,	ASCII 12)
     * \l	LF	(line feed,	ASCII 10)
     * \m	CR/LF	pair	(ASCII 13, 10)
     * \n	newline	(implementation dependent , e.g., CR/LF, CR, LF, LF/CR)
     * \q	double-quote	(ASCII 34)
     * \r	CR	(carriage return,	ASCII 13)
     * \t	HT	(horizontal tab,	ASCII 9)
     * \v	VT	(vertical tab,	ASCII 11)
     * \z	NUL	(no character,	ASCII 0)
     * \"	double-quote	(ASCII 34)
     * \x<  <hex-digit><hex-digit> char of these two hex digits
     * \\	backslash itself	(ASCII 92)
     *
     * */

    fun w_sBackSlashQuote(vm: ForthVM) {
        // non-urgent, but: don't implement \xXX form
        val s = vm.source.scanner.parse('"').strFromAddrLen(vm)
        val newS = s
            .replace("\\a", "\u0007")
            .replace("\\b", "\u0008")
            .replace("\\e", "\u001B")
            .replace("\\f", "\u000C")
            .replace("\\l", "\u0010")
            .replace("\\m", "\u000D\u0010")
            .replace("\\n", "\n")
            .replace("\\q", "\"")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\v", "\u000B")
            .replace("\\z", "\u0000")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")

        if (vm.interp.isInterpreting) {
            val strAddr = vm.appendStrToData(newS)
            vm.dstk.push(strAddr, newS.length)
        } else {
            vm.appendStr(newS)
        }
    }
}


