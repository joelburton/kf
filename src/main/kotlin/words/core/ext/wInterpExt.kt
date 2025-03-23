package kf.words.core.ext

import kf.D
import kf.ForthVM
import kf.IWordModule
import kf.Word

object wInterpExt: IWordModule {
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
        val (addr, len) = vm.scanner.parseName()
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
     *
     * When the input source is a string from EVALUATE, return false and
     * perform no other action.
     */

    fun w_refill(vm: ForthVM) {
        // fixme: not handling the "if a string from evaluate" yet

            val s = vm.source.readLineOrNull()
            if (s == null) {
                vm.dstk.push(0)

            } else {
                vm.scanner.fill(s)
                vm.dstk.push(1)
            }
    }
}