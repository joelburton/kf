package kf.words.doublenums

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wDoubleNums  : IWordClass {
    override val name = "DoubleNum"
    override val description: String = "Double numbers"
    override val words get() = arrayOf(
        Word("D.", ::w_dDot),
    )

    /** `d.` `( d1 d2 -- : print double-number )`
     *
     * Pops two integers from the data stack (`dstk`), combines them into a
     * single 64-bit long value, then converts and prints this value in the
     * current numeric base (`base`).
     *
     * The high-order 32 bits are taken from the first value popped, and the
     * low-order 32 bits are taken from the second value popped. The numeric
     * base is constrained between 2 and 36. The resulting string is output
     * followed by a space.
     */

    private fun w_dDot(vm: ForthVM) {
        val hi: Int = vm.dstk.pop()
        val lo: Int = vm.dstk.pop()
        val combined = (hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFFL)
        vm.io.print(combined.toString(vm.base.coerceIn(2, 36)) + " ")
    }
}