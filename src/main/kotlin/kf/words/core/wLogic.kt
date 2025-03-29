package kf.words.core

import kf.ForthVM
import kf.ForthVM.Companion.FALSE
import kf.ForthVM.Companion.TRUE
import kf.dict.IWordModule
import kf.dict.Word

object wLogic : IWordModule {
    override val name = "kf.words.core.wLogic"
    override val description = "Comparison and logic words"

    override val words
        get() = arrayOf(
            Word("0<", ::w_zeroLess),
            Word("0=", ::w_zeroEquals),
            Word("<", ::w_lessThan),
            Word("=", ::w_equals),
            Word(">", ::w_greaterThan),
            Word("AND", ::w_and),
            Word("MAX", ::w_max),
            Word("MIN", ::w_min),
            Word("OR", ::w_or),
            Word("XOR", ::w_xor),
            Word("INVERT", ::w_invert),
        )

    /** `0<` ( n -- flag ) Flag is true if and only if n is less than zero */

    fun w_zeroLess(vm: ForthVM) {
        vm.dstk.push(if (vm.dstk.pop() < 0) TRUE else FALSE)
    }

    /** `0=` ( x -- flag ) flag is true if and only if x is equal to zero */

    fun w_zeroEquals(vm: ForthVM) {
        vm.dstk.push(if (vm.dstk.pop() == 0) TRUE else FALSE)
    }

    /** `<` ( n1 n2 -- flag ) flag is true if and only if n1 is less than n2 */

    fun w_lessThan(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 < n2) TRUE else FALSE)
    }

    /** `=` ( x1 x2 -- flag ) flag is true if and only x1 == x2 */

    fun w_equals(vm: ForthVM) {
        vm.dstk.push(if (vm.dstk.pop() == vm.dstk.pop()) TRUE else FALSE)
    }

    /** `>` ( n1 n2 -- flag ) flag is true if and only if n1 > n2 */

    fun w_greaterThan(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 > n2) TRUE else FALSE)
    }

    /** `AND` ( x1 x2 -- x3 ) x3 is bit-by-bit logical "and" of x1 with x2 */

    fun w_and(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() and vm.dstk.pop())
    }

    /** `MAX` ( n1 n2 -- n3 ) n3 is the greater of n1 and n2 */

    fun w_max(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 > n2) n1 else n2)
    }

    /** `MIN` ( n1 n2 -- n3 ) n3 is the lesser of n1 and n2 */

    fun w_min(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 < n2) n1 else n2)
    }

    /** `OR` ( x1 x2 -- x3 ) x3 is the bit-by-bit inclusive-or of x1 with x2 */

    fun w_or(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() or vm.dstk.pop())
    }

    /** `XOR` ( x1 x2 -- x3 ) x3 is the bit-by-bit exclusive-or of x1 with x2 */

    fun w_xor(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() xor vm.dstk.pop())
    }

    /** `INVERT` ( x1 -- x2 ) Invert all bits of x1 */

    fun w_invert(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop().inv() and ForthVM.Companion.MAX_INT)
    }
}