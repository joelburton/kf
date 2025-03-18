package kf.words

import kf.ForthVM
import kf.ForthVM.Companion.FALSE
import kf.ForthVM.Companion.TRUE
import kf.IWordClass
import kf.Word

object wLogic : IWordClass {
    override val name = "Logic"
    override val description = "Comparison and logic words"

    override val words
        get() = arrayOf(
            Word("0<", ::w_zeroLess),
            Word("0=", ::w_zeroEquals),
            Word("<", ::w_lessThan),
            Word("=", ::w_equals),
            Word(">", ::w_gt),
            Word("AND", ::w_and),
            Word("MAX", ::w_max),
            Word("MIN", ::w_min),
            Word("OR", ::w_or),
            Word("XOR", ::w_xor),
            Word("INVERT", ::w_invert),
        )

    /** 0<   zero-less   CORE
     *
     * ( n -- flag )
     *
     * flag is true if and only if n is less than zero.
     */

    fun w_zeroLess(vm: ForthVM) {
        val n = vm.dstk.pop()
        vm.dstk.push(if (n < 0) TRUE else FALSE)
    }

    /** 0=  zero-equals     CORE
     *
     * ( x -- flag )
     *
     * flag is true if and only if x is equal to zero.
     */

    fun w_zeroEquals(vm: ForthVM) {
        val n = vm.dstk.pop()
        vm.dstk.push(if (n == 0) TRUE else FALSE)
    }

    /** <    less-than   CORE
     *
     * ( n1 n2 -- flag )
     *
     * flag is true if and only if n1 is less than n2.
     */

    fun w_lessThan(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 < n2) TRUE else FALSE)
    }

    /** =    equals  CORE
     *
     * ( x1 x2 -- flag )
     *
     * flag is true if and only if x1 is bit-for-bit the same as x2.
     */

    fun w_equals(vm: ForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(if (x2 == x1) TRUE else FALSE)
    }

    /** >    greater-than    CORE
     *
     * ( n1 n2 -- flag )
     *
     * flag is true if and only if n1 is greater than n2.
     */

    fun w_gt(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 > n2) TRUE else FALSE)
    }

    /** AND  CORE
     *
     * ( x1 x2 -- x3 )
     *
     * x3 is the bit-by-bit logical "and" of x1 with x2.
     */

    fun w_and(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() and vm.dstk.pop())
    }

    /** MAX  CORE
     *
     * ( n1 n2 -- n3 )
     *
     * n3 is the greater of n1 and n2.
     */

    fun w_max(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 > n2) n1 else n2)
    }

    /** MIN  CORE
     *
     * ( n1 n2 -- n3 )
     *
     * n3 is the lesser of n1 and n2.
     */

    fun w_min(vm: ForthVM) {
        val n2 = vm.dstk.pop()
        val n1 = vm.dstk.pop()
        vm.dstk.push(if (n1 < n2) n1 else n2)
    }

    /** OR   CORE
     *
     * ( x1 x2 -- x3 )
     *
     * x3 is the bit-by-bit inclusive-or of x1 with x2.
     */

    fun w_or(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() or vm.dstk.pop())
    }

    /** XOR  x-or    CORE
     *
     * ( x1 x2 -- x3 )
     *
     * x3 is the bit-by-bit exclusive-or of x1 with x2.
     */

    fun w_xor(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop() xor vm.dstk.pop())
    }

    /** INVERT   CORE
     *
     * ( x1 -- x2 )
     *
     * Invert all bits of x1, giving its logical inverse x2.
     */

    fun w_invert(vm: ForthVM) {
        vm.dstk.push(vm.dstk.pop().inv())
    }
}