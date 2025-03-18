package kf.primitives

import kf.ForthError
import kf.ForthVM
import kf.ForthVM.Companion.TRUE
import kf.ForthVM.Companion.FALSE
import kf.Word
import kf.WordClass
import kotlin.math.sqrt

object WMathLogic : WordClass {
    override val name = "MathLogic"
    override val primitives get() = arrayOf(
        Word("not", ::w_not ) ,
        Word(">=", ::w_gte ) ,
        Word("<=", ::w_lte ) ,
        Word("<>", ::w_ne ) ,
        Word("sqrt", ::w_sqrt ) ,
        Word("true", ::w_true ) ,
        Word("false", ::w_false ) ,
        // TODO:
        // 0<>
    )




    /**  ( n1 -- ~n1 : bitwise NOT ) */
    fun w_not(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop().inv() and ForthVM.Companion.MAX_INT)
    }









    /**  ( n1 n2 -- n1>=n2? : greater-than-or-equal ) */
    fun w_gte(vm: ForthVM) {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b >= a) TRUE else FALSE)
    }

    /**  ( n1 n2 -- n1<=n2? : less-than-or-equal ) */
    fun w_lte(vm: ForthVM) {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b <= a) TRUE else FALSE)
    }

    /**  ( n1 n2  -- n1 != n2 : not equal  ) */
    fun w_ne(vm: ForthVM) {
        val a: Int = vm.dstk.pop()
        val b: Int = vm.dstk.pop()
        vm.dstk.push(if (a == b) FALSE else TRUE)
    }


    /**  ( n -- int(sqrt(n)) : square root ) */
    fun w_sqrt(vm: ForthVM) {
        val res = sqrt(vm.dstk.pop().toDouble())
        vm.dstk.push(res.toInt())
    }

    /**  ( n -- true : pushes -1 {true} to stack ) */
    fun w_true(vm: ForthVM) {
        vm.dstk.push(TRUE)
    }

    /**  ( n -- false : pushes 0 {false} to stack ) */
    fun w_false(vm: ForthVM) {
        vm.dstk.push(FALSE)
    }
}