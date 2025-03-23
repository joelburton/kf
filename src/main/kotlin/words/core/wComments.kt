package kf.words.core

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wComments: IWordModule {
    override val name = "kf.words.core.wComments"
    override val description = "Comments"

    override val words
        get() = arrayOf(
            Word("(", imm = true, fn = ::w_parenComment),
        )

    /** (     CORE
     *
     * Compilation:
     * Perform the execution semantics given below.
     *
     * Execution:
     *      ( "ccc<paren>" -- )
     * Parse ccc delimited by ) (right parenthesis). ( is an immediate word.
     *
     * The number of characters in ccc may be zero to the number of characters
     * in the parse area.
     */

    fun w_parenComment(vm: ForthVM) {
        vm.scanner.parse(')')
    }

}