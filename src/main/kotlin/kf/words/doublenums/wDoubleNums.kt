package kf.words.doublenums

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord

// 8.6.1.0360 2CONSTANT
//8.6.1.0390 2LITERAL
//8.6.1.0440 2VARIABLE
//8.6.1.1040 D+
//8.6.1.1050 D-
//8.6.1.1060 D.
//8.6.1.1070 D.R
//8.6.1.1075 D0<
//8.6.1.1080 D0=
//8.6.1.1090 D2*
//8.6.1.1100 D2/
//8.6.1.1110 D<
//8.6.1.1120 D=
//8.6.1.1140 D>S
//8.6.1.1160 DABS
//8.6.1.1210 DMAX
//8.6.1.1220 DMIN
//8.6.1.1230 DNEGATE
//8.6.1.1820 M*/
//8.6.1.1830 M+

// ext:
// 8.6.2.0420 2ROT
//8.6.2.0435 2VALUE
//8.6.2.1270 DU<

object wDoubleNums : IWordModule {
    override val name = "kf.words.doublenums.wDoubleNums"
    override val description: String = "Double numbers"
    override val words
        get() = arrayOf<IWord>(
            Word("D.", ::w_dDot),
        )

    /** `D.` `( d1 d2 -- )` Print double-number
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

    fun w_dDot(vm: ForthVM) {
        val dbl = vm.dstk.dblPop()
        vm.io.print(dbl.toString(vm.base) + " ")
    }
}