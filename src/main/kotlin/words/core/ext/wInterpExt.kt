package kf.words.core.ext

import kf.D
import kf.ForthVM
import kf.IWordClass
import kf.Word

object wInterpExt: IWordClass {
    override val name = "core.ext.interpExt"
    override val description = "Interpreter extra words"
    override val words = arrayOf(
        Word("PARSE-NAME", ::w_parseName),
        Word("REFILL", ::w_refill),

        )

    /** PARSE-NAME   CORE EXT
     *
     * ( "<spaces>name<space>" -- c-addr u )
     *
     * Skip leading space delimiters. Parse name delimited by a space.
     *
     * c-addr is the address of the selected string within the input buffer and
     * u is its length in characters. If the parse area is empty or contains
     * only white space, the resulting string has length zero.
     */

    fun w_parseName(vm: ForthVM) {
        val (addr, len) = vm.scanner.parseName()
        if (D) vm.dbg(2, "w_parseName: addr=$addr len=$len")
        vm.dstk.push(addr, len)
    }

    /** REFILL          CORE EXT
     *
     * ( -- flag )
     *
     * Attempt to fill the input buffer from the input source, returning a true
     * flag if successful.
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

        val input = vm.io.readLineOrNull(false)
        if (input == null) {
            vm.dstk.push(0)
        } else {
            vm.scanner.fill(input)
            vm.dstk.push(1)
        }
    }


}