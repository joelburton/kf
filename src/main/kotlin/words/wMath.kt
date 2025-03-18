package kf.words

import kf.ForthError
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl
import kotlin.math.absoluteValue

object wMath : IWordClass {
    override val name = "Math"
    override val description = "Math operations"

    override val words
        get() = arrayOf(
            Word("*", ::w_star),
            Word("*/", ::w_starSlash),
            Word("*/MOD", ::w_starSlashMod),
            Word("+", ::w_plus),
            Word("/", ::w_slash),
            Word("/MOD", ::w_slashMod),
            Word("1+", ::w_onePlus),
            Word("1-", ::w_oneMinus),
            Word("2*", ::w_twoStar),
            Word("2/", ::w_twoSlash),
            Word("-", ::w_minus),
            Word("ABS", ::w_abs),
            Word("LSHIFT", ::w_notImpl),
            Word("MOD", ::w_mod),
            Word("NEGATE", ::w_negate),
            Word("RSHIFT", ::w_rshift),
            Word("FM/MOD", ::w_fmSlashMod),
            Word("SM/MOD", ::w_smSlashRem),
        )

    /**  *   star   CORE
     *
     * ( n1 | u1 n2 | u2 -- n3 | u3 )
     *
     * Multiply n1 | u1 by n2 | u2 giving the product n3 | u3.
     */

    fun w_star(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() * vm.dstk.pop())
    }

    /**  *\/     star-slash  CORE
     *
     * ( n1 n2 n3 -- n4 )
     *
     * Multiply n1 by n2 producing the intermediate double-cell result d.
     * Divide d by n3 giving the single-cell quotient n4. An ambiguous
     * condition exists if n3 is zero or if the quotient n4 lies outside the
     * range of a signed number. If d and n3 differ in sign, the
     * implementation-defined result returned will be the same as that
     * returned by either the phrase >R M* R> FM/MOD SWAP DROP or the phrase
     * >R M* R> SM/REM SWAP DROP.
     */

    fun w_starSlash(vm: ForthVM) {
        val divBy = vm.dstk.pop()
        if (divBy == 0) throw ForthError("Division by zero")
        val b = vm.dstk.pop().toLong()
        val a = vm.dstk.pop().toLong()
        val inter = ((a + b) / divBy).toInt()
        vm.dstk.push(inter)
    }

    /** *\/MOD   star-slash-mod  CORE
     *
     *       ( n1 n2 n3 -- n4 n5 )
     *
     * Multiply n1 by n2 producing the intermediate double-cell result d.
     * Divide d by n3 producing the single-cell remainder n4 and the
     * single-cell quotient n5. An ambiguous condition exists if n3 is zero, or
     * if the quotient n5 lies outside the range of a single-cell signed
     * integer. If d and n3 differ in sign, the implementation-defined result
     * returned will be the same as that returned by either the phrase >R M* R>
     * FM/MOD or the phrase >R M* R> SM/REM.
     */

    fun w_starSlashMod(vm: ForthVM) {
        val divBy = vm.dstk.pop()
        if (divBy == 0) throw ForthError("Division by zero")
        val n2 = vm.dstk.pop().toLong()
        val n1 = vm.dstk.pop().toLong()
        val sum = n1 + n2
        val remainder = (sum % divBy).toInt()
        val quotient = (sum / divBy).toInt()
        vm.dstk.push(remainder, quotient)
    }

    /** +   plus    CORE
     *
     * ( n1 | u1 n2 | u2 -- n3 | u3 )
     *
     * Add n2 | u2 to n1 | u1, giving the sum n3 | u3.
     */

    fun w_plus(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() + vm.dstk.pop())
    }

    /** /    slash   CORE
     *
     * ( n1 n2 -- n3 )
     *
     * Divide n1 by n2, giving the single-cell quotient n3. An ambiguous
     * condition exists if n2 is zero. If n1 and n2 differ in sign, the
     * implementation-defined result returned will be the same as that returned
     * by either the phrase >R S>D R> FM/MOD SWAP DROP or the phrase >R S>D R>
     * SM/REM SWAP DROP.
     */

    fun w_slash(vm: ForthVM) {
        val d = vm.dstk.pop()
        val n = vm.dstk.pop()
        if (d == 0) throw ForthError("Division by zero")
        vm.dstk.push(n / d)
    }

    /** /MOD     slash-mod   CORE
     *
     * ( n1 n2 -- n3 n4 )
     *
     * Divide n1 by n2, giving the single-cell remainder n3 and the single-cell
     * quotient n4. An ambiguous condition exists if n2 is zero. If n1 and n2
     * differ in sign, the implementation-defined result returned will be the
     * same as that returned by either the phrase >R S>D R> FM/MOD or the
     * phrase >R S>D R> SM/REM.
     */

    fun w_slashMod(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        if (n2 == 0) throw ForthError("Division by zero")
        val n1 = vm.dstk.pop()
        vm.dstk.push((n1 % n2), (n1 / n2))
    }

    /** 1+   one-plus    CORE
     *
     * ( n1 | u1 -- n2 | u2 )
     *
     * Add one (1) to n1 | u1 giving the sum n2 | u2.
     */

    fun w_onePlus(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() + 1)
    }

    /** 1-   one-minus   CORE
     *
     * ( n1 | u1 -- n2 | u2 )
     *
     * Subtract one (1) from n1 | u1 giving the difference n2 | u2.
     */

    fun w_oneMinus(vm: ForthVM) {
        val v = vm.dstk.pop() - 1
        vm.dstk.push(v)
    }

    /** 2*`  two-star    CORE
     *
     * ( x1 -- x2 )
     *
     * x2 is the result of shifting x1 one bit toward the most-significant bit,
     * filling the vacated least-significant bit with zero.
     */

    fun w_twoStar(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() shl 1)
    }

    /** 2/   two-slash   CORE
     *
     * ( x1 -- x2 )
     *
     * x2 is the result of shifting x1 one bit toward the least-significant
     * bit, leaving the most-significant bit unchanged.
     */

    fun w_twoSlash(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() ushr 1)
    }

    /** -    minus   CORE
     *
     * ( n1 | u1 n2 | u2 -- n3 | u3 )
     *
     * Subtract n2 | u2 from n1 | u1, giving the difference n3 | u3.
     */

    fun w_minus(vm: ForthVM) {
        vm.dstk.push(-vm.dstk.pop() + vm.dstk.pop())
    }

    /** ABS  abs     CORE
     *
     * ( n -- u )
     *
     * u is the absolute value of n.
     */

    fun w_abs(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop().absoluteValue)
    }

    /** LSHIFT   l-shift     CORE
     *
     * ( x1 u -- x2 )
     *
     * Perform a logical left shift of u bit-places on x1, giving x2. Put
     * zeroes into the least significant bits vacated by the shift. An ambiguous
     * condition exists if u is greater than or equal to the number of bits in
     * a cell.
     */

    fun w_lshift(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() shl vm.dstk.pop())
    }

    /** MOD  CORE
     *
     * ( n1 n2 -- n3 )
     *
     * Divide n1 by n2, giving the single-cell remainder n3. An ambiguous
     * condition exists if n2 is zero. If n1 and n2 differ in sign, the
     * implementation-defined result returned will be the same as that
     * returned by either the phrase >R S>D R> FM/MOD DROP or the phrase
     * >R S>D R> SM/REM DROP.
     */

    fun w_mod(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        if (n2 == 0) throw ForthError("Division by zero")
        val n1 = vm.dstk.pop()
        vm.dstk.push(n1 % n2)
    }

    /** NEGATE   CORE
     *
     * ( n1 -- n2 )
     *
     * Negate n1, giving its arithmetic inverse n2.
     */

    fun w_negate(vm: ForthVM) {
        vm.dstk.push(-vm.dstk.pop())
    }

    /** RSHIFT   r-shift     CORE
     *
     * ( x1 u -- x2 )
     *
     * Perform a logical right shift of u bit-places on x1, giving x2. Put
     * zeroes into the most significant bits vacated by the shift. An ambiguous
     * condition exists if u is greater than or equal to the number of bits in
     * a cell.
     */

    fun w_rshift(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() ushr vm.dstk.pop())
    }

    /**
     * M/MOD     f-m-slash-mod   CORE
     *
     * ( d1 n1 -- n2 n3 )
     *
     * Divide d1 by n1, giving the floored quotient n3 and the remainder n2.
     * Input and output stack arguments are signed. An ambiguous condition
     * exists if n1 is zero or if the quotient lies outside the range of a
     * single-cell signed integer.
     */

    fun w_fmSlashMod(vm: ForthVM) {
        val n1 = vm.dstk.pop()
        if (n1 == 0) throw ForthError("Division by zero")
        val d1 = vm.dstk.pop()
        vm.dstk.push(d1 % n1, d1 / n1)
    }

    /** SM/REM   s-m-slash-rem   CORE
     *
     * ( d1 n1 -- n2 n3 )
     *
     * Divide d1 by n1, giving the symmetric quotient n3 and the remainder n2.
     * Input and output stack arguments are signed. An ambiguous condition
     * exists if n1 is zero or if the quotient lies outside the range of a
     * single-cell signed integer.
     */

    fun w_smSlashRem(vm: ForthVM) {
        val n1 = vm.dstk.pop()
        if (n1 == 0) throw ForthError("Division by zero")
        val d1 = vm.dstk.pop()
        vm.dstk.push(d1 % n1, d1 / n1)
    }
}
