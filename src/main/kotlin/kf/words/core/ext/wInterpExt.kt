package kf.words.core.ext

import kf.D
import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word

object wInterpExt : IWordModule {
    override val name = "kf.words.core.ext.wInterpExt"
    override val description = "Interpreter extra words"
    override val words = arrayOf(
        Word("PARSE-NAME", ::w_parseName),
        Word("REFILL", ::w_refill),

        )

    /** `PARSE-NAME` ( "<spaces>name<space>" -- c-addr u ) Parse name
     *
     * Skip leading space delimiters. Parse name delimited by a space.
     *
     * c-addr is the address of the selected string within the input buffer and
     * u is its length in characters. If the parse area is empty or contains
     * only white space, the resulting string has length zero.
     */

    fun w_parseName(vm: ForthVM) {
        val (addr, len) = vm.source.scanner.parseName()
        if (D) vm.dbg(3, "w_parseName: addr=$addr len=$len")
        vm.dstk.push(addr, len)
    }

    /** `REFILL` ( -- flag ) Refill input buffer from source
     *
     * When the input source is the user input device, attempt to receive input
     * into the terminal input buffer. If successful, make the result the input
     * buffer, set >IN to zero, and return true. Receipt of a line containing
     * no characters is considered successful. If there is no input available
     * from the current input source, return false.
     */

    fun w_refill(vm: ForthVM) {
        val nChars = vm.source.scanner.nChars

        if (D) vm.dbg(3, "w_refill: $nChars at ${vm.inPtr} ")

        // Special case: a parser hadn't reached the end of the line, and
        // nothing called nextLine. This is probably because eval or
        // include ran to push a new input source on top. Rather than refilling
        // and not finishing the interpretation of the line that called
        // eval/include, simple report that a line was read and it can
        // continue where it left off.
        if (vm.inPtr < nChars) {
            vm.dstk.push(1)
            return
        }

        val s = vm.source.readLineOrNull()
        if (s == null) {
            vm.dstk.push(0)
        } else {
            vm.source.scanner.fill(s)
            vm.dstk.push(1)
        }
    }
}