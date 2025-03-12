package kf

import kf.ForthVM.Companion.TRUE
import kf.ForthVM.Companion.FALSE
import kotlin.math.sqrt

class WMathLogic(val vm: ForthVM) : WordClass {
    override val name = "MathLogic"
    override val primitives: Array<Word> = arrayOf(
        Word("and") { w_and() },
        Word("or") {  w_or() },
        Word("not") {  w_not() },
        Word("inv") {  w_inv() },
        Word("negate") {  w_inv() },
        Word("invert") {  w_not() },  // synonym for "not"
        Word("xor") {  w_xor() },
        Word("+") {  w_add() },
        Word("-") {  w_sub() },
        Word("*") {  w_mul() },
        Word("/") {  w_div() },
        Word("mod") {  w_mod() },
        Word("=") {  w_eq() },
        Word(">") {  w_gt() },
        Word("<") {  w_lt() },
        Word(">=") {  w_gte() },
        Word("<=") {  w_lte() },
        Word("<>") {  w_ne() },
        Word("0=") {  w_eq0() },
        Word("1+") {  w_inc() },
        Word("1-") {  w_dec() },
        Word("sqrt") {  w_sqrt() },
        Word("true") {  w_true() },
        Word("false") {  w_false() },  
        // TODO:
        // abs
        // min, max
        // rshift  x u -- x2  put 0s in new bits
        // lshift  x u -- x2   (same)
        // 0<>
    )

    /**  ( n1 n2 -- n1 & n2 : bitwise AND ) */
    fun w_and() {
        vm.dstk.push(vm.dstk.pop() and vm.dstk.pop())
    }

    /**  ( n1 n2 -- n1 | n2 : bitwise OR ) */
    fun w_or() {
        vm.dstk.push(vm.dstk.pop() or vm.dstk.pop())
    }

    /**  ( n1 -- ~n1 : bitwise NOT ) */
    fun w_not() {
        vm.dstk.push(vm.dstk.pop().inv() and ForthVM.MAX_INT)
    }

    /**  ( n1 -- -n1 : invert sign ) */
    fun w_inv() {
        vm.dstk.push(vm.dstk.pop().inv() and ForthVM.MAX_INT)
    }

    /**  ( n1 n2 -- n1 ^ n2 : bitwise XOR ) */
    fun w_xor() {
        vm.dstk.push(vm.dstk.pop() xor vm.dstk.pop())
    }

    /**  ( n1 n2 -- n1 + n2 : addition ) */
    fun w_add() {
        vm.dstk.push(vm.dstk.pop() + vm.dstk.pop())
    }

    /**  ( n1 n2 -- n1-n2 : subtraction ) */
    fun w_sub() {
        vm.dstk.push(-vm.dstk.pop() + vm.dstk.pop())
    }

    /**  ( n1 n2 -- n1 * n2 : multiplication ) */
    fun w_mul() {
        vm.dstk.push(vm.dstk.pop() * vm.dstk.pop())
    }

    /**  ( n1 n2 -- n1 / n2 : division ) */
    fun w_div() {
        val d = vm.dstk.pop()
        val n = vm.dstk.pop()
        vm.dstk.push(n / d)
    }

    /**  ( n1 n2 -- n1 % n2 : remainder ) */
    fun w_mod() {
        val d = vm.dstk.pop()
        val n = vm.dstk.pop()
        vm.dstk.push(n % d)
    }

    /**  ( n1 n2 -- n1==n2? : equality check ) */
    fun w_eq() {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (a == b) TRUE else FALSE)
    }

    /**  ( n1 n2 -- n1>n2? : greater than ) */
    fun w_gt() {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b > a) TRUE else FALSE)
    }

    /**  ( n1 n2 -- n1<n2></n2>? : less than ) */
    fun w_lt() {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b < a) TRUE else FALSE)
    }

    /**  ( n1 n2 -- n1>=n2? : greater-than-or-equal ) */
    fun w_gte() {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b >= a) TRUE else FALSE)
    }

    /**  ( n1 n2 -- n1<=n2? : less-than-or-equal ) */
    fun w_lte() {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b <= a) TRUE else FALSE)
    }

    /**  ( n -- n+1  : increment ) */
    fun w_inc() {
        vm.dstk.push(vm.dstk.pop() + 1)
    }

    /**  ( n -- n-1  : decrement ) */
    fun w_dec() {
        val v = vm.dstk.pop() - 1
        vm.dstk.push(v)
    }

    /**  ( n1 n2  -- n1 != n2 : not equal  ) */
    fun w_ne() {
        val a: Int = vm.dstk.pop()
        val b: Int = vm.dstk.pop()
        vm.dstk.push(if (a == b) FALSE else TRUE)
    }

    /**  ( n -- n==0? : not equal check ) */
    fun w_eq0() {
        val a: Int = vm.dstk.pop()
        val out = if (a == 0) TRUE else FALSE
        vm.dstk.push(out)
    }

    /**  ( n -- int(sqrt(n)) : square root ) */
    fun w_sqrt() {
        val res = sqrt(vm.dstk.pop().toDouble())
        vm.dstk.push(res.toInt())
    }

    /**  ( n -- true : pushes -1 {true} to stack ) */
    fun w_true() {
        vm.dstk.push(TRUE)
    }

    /**  ( n -- false : pushes 0 {false} to stack ) */
    fun w_false() {
        vm.dstk.push(FALSE)
    }

    /** ( n -- -n : negate sign ) */
    fun w_negate() {
        vm.dstk.push(-vm.dstk.pop())
    }
}