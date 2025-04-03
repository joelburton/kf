package kf.words.core

import kf.DivisionByZero
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kotlin.math.absoluteValue

object wMath : IWordModule {
    override val name = "kf.words.core.wMath"
    override val description = "Math operations"

    override val words
        get() = arrayOf<Word>(
            Word("+", ::w_plus),
            Word("-", ::w_minus),
            Word("*", ::w_star),
            Word("/", ::w_slash),

            Word("NEGATE", ::w_negate),
            Word("LSHIFT", ::w_lshift),
            Word("RSHIFT", ::w_rshift),
            Word("ABS", ::w_abs),

            Word("2*", ::w_twoStar),
            Word("2/", ::w_twoSlash),
            Word("*/", ::w_starSlash),

            Word("MOD", ::w_mod),
            Word("/MOD", ::w_slashMod),
            Word("*/MOD", ::w_starSlashMod),
            Word("FM/MOD", ::w_fmSlashMod),
            Word("SM/MOD", ::w_smSlashRem),

            Word("1+", ::w_onePlus),
            Word("1-", ::w_oneMinus),
        )

    /** `+` ( n1 n2 -- n3 ) add */

    fun w_plus(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop() + vm.dstk.pop())
    }

    /** `-` ( n1 n2 -- n3 ) n1 - n2 => n3 */

    fun w_minus(vm: IForthVM) {
        vm.dstk.push(-vm.dstk.pop() + vm.dstk.pop())
    }

    /** `*` ( n1 n2 -- n3 ) Multiply n1 by n2 giving the product u3 */

    fun w_star(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop() * vm.dstk.pop())
    }

    /** `/` ( n1 n2 -- n3 ) */

    fun w_slash(vm: IForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        if (n2 == 0) throw DivisionByZero()
        vm.dstk.push(n1 / n2)
    }


    /** `NEGATE` ( n1 -- n2 ) -n1 */

    fun w_negate(vm: IForthVM) {
        vm.dstk.push(-vm.dstk.pop())
    }

    /** `LSHIFT` ( x1 u -- x2 ) logical left shift x1 u times */

    fun w_lshift(vm: IForthVM) {
        val u = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(x1 shl u)
    }

    /** `RSHIFT` ( x1 u -- x2 ) logical right shift u places */

    fun w_rshift(vm: IForthVM) {
        val u = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(x1 ushr u)
    }

    /** `ABS` ( n -- u ) u is the absolute value of n */

    fun w_abs(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop().absoluteValue)
    }


    /** `2*` ( x1 -- x2 ) shift left (mult by 2) */

    fun w_twoStar(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop() shl 1)
    }

    /** `2/`  ( x1 -- x2 ) shift right (div by 2) */

    fun w_twoSlash(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop() ushr 1)
    }

    /**  `* /` ( n1 n2 n3 -- n4 ) n1 * n2 / n3 => n4 */

    fun w_starSlash(vm: IForthVM) {
        val divBy = vm.dstk.pop()
        if (divBy == 0) throw DivisionByZero()
        val n2 = vm.dstk.pop().toLong()
        val n1 = vm.dstk.pop().toLong()
        vm.dstk.push(((n1 * n2) / divBy).toInt())
    }


    /** `MOD` ( n1 n2 -- n3 ) remainder of n1 / n2 */

    fun w_mod(vm: IForthVM) {
        val n2 = vm.dstk.pop()
        if (n2 == 0) throw DivisionByZero()
        val n1 = vm.dstk.pop()
        vm.dstk.push(n1 % n2)
    }

    /** `/MOD` ( n1 n2 -- n3 n4 ) n1 divmod d2 => remainder quotient */

    fun w_slashMod(vm: IForthVM) {
        val n2 = vm.dstk.pop()
        if (n2 == 0) throw DivisionByZero()
        val n1 = vm.dstk.pop()
        vm.dstk.push((n1 % n2), (n1 / n2))
    }

    /** `* /MOD` ( n1 n2 n3 -- n4 n5 ) n1 * n2 / n3 => remainder quotient */

    fun w_starSlashMod(vm: IForthVM) {
        val divBy = vm.dstk.pop()
        if (divBy == 0) throw DivisionByZero()
        val n2 = vm.dstk.pop().toLong()
        val n1 = vm.dstk.pop().toLong()
        val sum = n1 * n2
        val remainder = (sum % divBy).toInt()
        val quotient = (sum / divBy).toInt()
        vm.dstk.push(remainder, quotient)
    }

    /** FM/MOD ( d1 n1 -- n2 n3 ) Floored quotient and remainder of d1/n1 */

    fun w_fmSlashMod(vm: IForthVM) {  // -7 fm/mod 3 = -3, 2
        val n1 = vm.dstk.pop().toLong()
        if (n1 == 0L) throw DivisionByZero()
        val d1 = vm.dstk.dblPop()
        val quot = d1.floorDiv(n1)
        val remainder = d1.mod(n1)
        vm.dstk.push(quot.toInt(), remainder.toInt())
    }

    /** SM/REM ( d1 n1 -- n2 n3 ) Symmetric quotient and remainder of d1/n1 */

    fun w_smSlashRem(vm: IForthVM) { // -7 sm/mod 3 = -2, -1
        val n1 = vm.dstk.pop().toLong()
        if (n1 == 0L) throw DivisionByZero()
        val d1 = vm.dstk.dblPop()
        vm.dstk.push((d1 / n1).toInt(), (d1 % n1).toInt())
    }


    /** `1+` ( n1 -- n2 ) add 1 to n1 */

    fun w_onePlus(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop() + 1)
    }

    /** `1-` ( n1 -- n2 ) subtract 1 from n1 */

    fun w_oneMinus(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop() - 1)
    }
}
