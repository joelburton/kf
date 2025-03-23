package kf.words.core

import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.numToStr

object wNumIO : IWordModule {
    override val name = "kf.words.core.IO"
    override val description = "General input and output"

    override val words
        get() = arrayOf(
            Word("DECIMAL", ::w_decimal),
            Word("BASE", ::w_base),
            Word(".", ::w_dot),
        )


    /** `DECIMAL` ( -- ) Set the numeric conversion radix to ten (decimal) */

    fun w_decimal(vm: ForthVM) {
        vm.base = 10
    }

    /** `BASE` ( -- a-addr ) a-addr is address of cell containing radix */

    fun w_base(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_BASE)
    }

    /** `.` ( n -- ) Display n in free field format */

    fun w_dot(vm: ForthVM) {
        vm.io.print("${vm.dstk.pop().numToStr(vm.base)} ")
    }
}