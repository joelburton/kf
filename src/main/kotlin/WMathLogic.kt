package kf

import kotlin.math.sqrt

class WMathLogic(val vm: ForthVM) {
    companion object {
        val MAX_INT: Int = 0x7fffffff
        val TRUE: Int = -1
        val FALSE: Int = 0
    }

    val primitives: Array<Word> = arrayOf<Word>(
        Word("and") { _ ->
            w_and()
        },
        Word("or") { _ -> w_or() },
        Word("not") { _ -> w_not() },
        Word("invert") { _ -> w_not() },  // synonym for "not"
        Word("xor") { _ -> w_xor() },
        Word("inv") { _ -> w_inv() },
        Word("+") { _ -> w_add() },
        Word("-") { _ -> w_sub() },
        Word("*") { _ -> w_mul() },
        Word("/") { _ -> w_div() },
        Word("mod") { _ -> w_mod() },
        Word("=") { _ -> w_eq() },
        Word(">") { _ -> w_gt() },
        Word("<") { _ -> w_lt() },
        Word(">=") { _ -> w_gte() },
        Word("<=") { _ -> w_lte() },
        Word("<>") { _ -> w_ne() },
        Word("0=") { _ -> w_eq0() },
        Word("1+") { _ -> w_inc() },
        Word("1-") { _ -> w_dec() },
        Word("sqrt") { _ -> w_sqrt() },
        Word("true") { _ -> w_true() },
        Word("false") { _ -> w_false() },  // abs
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
        vm.dstk.push(vm.dstk.pop().inv())
    }

    /**  ( n1 n2 -- n1 ^ n2 : bitwise XOR ) */
    fun w_xor() {
        vm.dstk.push(vm.dstk.pop() xor vm.dstk.pop())
    }

    /**  ( n1 -- -n1 : invert sign ) */
    fun w_inv() {
        vm.dstk.push(-vm.dstk.pop())
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
        val `val` = vm.dstk.pop() - 1
        vm.dbg("w_dec: val=%d", `val`)
        vm.dstk.push(`val`)
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
        if (D) vm.dbg("w_eq0: a=%d out=%d", a, out)
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
}