package kf.words.core.ext

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wLogicExt : IWordClass {
    override val name = "core.ext.logicExt"
    override val description = "Comparison and logic words"

    override val words
        get() = arrayOf(
            Word("0<>", ::w_notImpl),
            Word("0>", ::w_notImpl),
            Word("<>", ::w_ne),
            Word("TRUE", ::w_true),
            Word("FALSE", ::w_false),
            Word("WITHIN", ::w_notImpl),
        )

    /**  ( n1 n2  -- n1 != n2 : not equal  ) */
    fun w_ne(vm: ForthVM) {
        val a: Int = vm.dstk.pop()
        val b: Int = vm.dstk.pop()
        vm.dstk.push(if (a == b) ForthVM.Companion.FALSE else ForthVM.Companion.TRUE)
    }

    /**  ( n -- true : pushes -1 {true} to stack ) */
    fun w_true(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.TRUE)
    }

    /**  ( n -- false : pushes 0 {false} to stack ) */
    fun w_false(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.FALSE)
    }
}