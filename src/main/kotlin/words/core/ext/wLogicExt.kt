package kf.words.core.ext

import kf.ForthVM
import kf.ForthVM.Companion.FALSE
import kf.ForthVM.Companion.TRUE
import kf.IWordModule
import kf.Word
import kf.w_notImpl

object wLogicExt : IWordModule {
    override val name = "kf.words.core.ext.wLogicExt"
    override val description = "Comparison and logic words"

    override val words
        get() = arrayOf(
            Word("0<>", ::w_zeroNotEquals),
            Word("0>", ::w_zeroGreater),
            Word("<>", ::w_notEquals),
            Word("TRUE", ::w_true),
            Word("FALSE", ::w_false),
            Word("WITHIN", ::w_within),
        )

    /**  ( n1 n2  -- n1 != n2 : not equal  ) */
    fun w_notEquals(vm: ForthVM) {
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

    /** `0>` ( n -- f ) Is n greater than zero? */
    fun w_zeroGreater(vm: ForthVM) {
        val a: Int = vm.dstk.pop()
        vm.dstk.push(if (a > 0) TRUE else FALSE)
    }

    /** `0<>` ( n -- f ) Is n not equal to zero? */
    fun w_zeroNotEquals(vm: ForthVM) {
        val a: Int = vm.dstk.pop()
        vm.dstk.push(if (a == 0) FALSE else TRUE)
    }

    /** `WITHIN` ( n1 n2 n3 -- flag ) Test n1 within  n2 and n3
     *
     * true if either (n2 < n3 and (n2 <= n1 and n1 < n3))
     *             or (n2 > n3 and (n2 <= n1  or n1 < n3)) is true
     * */

    fun w_within(vm: ForthVM) {
        val n3 = vm.dstk.pop()
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(
            if ((n2 < n3 && (n2 <= n1 && n1 < n3))
                || (n2 > n3 && (n2 <= n1 || n1 < n3))
            ) TRUE else FALSE
        )
    }
}

