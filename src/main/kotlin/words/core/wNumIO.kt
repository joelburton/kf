package kf.words.core

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.numToStr

object wNumIO : IWordClass {
    override val name = "IO"
    override val description = "General input and output"

    override val words
        get() = arrayOf(
            Word("DECIMAL", ::w_decimal),
            Word("BASE", ::w_base),
            Word(".", ::w_dot),
        )


    /** DECIMAL  CORE
     *
     * ( -- )
     *
     * Set the numeric conversion radix to ten (decimal).
     */

    fun w_decimal(vm: ForthVM) {
        vm.base = 10
    }

    /** BASE     CORE
     *
     * ( -- a-addr )
     *
     * a-addr is the address of a cell containing the current number-conversion
     * radix {{2...36}}.
     */

    fun w_base(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_BASE)
    }

    /** .   dot     CORE
     *
     * ( n -- )
     *
     * Display n in free field format.
     */

    fun w_dot(vm: ForthVM) {
        vm.io.print("${vm.dstk.pop().numToStr(vm.base)} ")
    }
}