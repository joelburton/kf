package kf.words.core

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord
import kotlin.math.absoluteValue

object wFormatting: IWordModule {
    override val name = "kf.words.core.wFormatting"
    override val description = "Picture words for numbers"
    var pict = StringBuilder()

    override val words
        get() = arrayOf<IWord>(
            Word("#", ::w_numberSign),
            Word("#>", ::w_numberSignGreater),
            Word("#S", ::w_numberSignS),
            Word("<#", ::w_lessNumberSign),
            Word("HOLD", ::w_hold),
            Word("SIGN", ::w_sign),
        )

    /** `#` ( d1 -- d2 ) d1 / base (-> d2) and add remainder to string */

    fun w_numberSign(vm: ForthVM) {
        val n = vm.dstk.dblPop()
        val rem = n % vm.base
        val quot = n / vm.base
        pict.append(rem.absoluteValue.toString(vm.base))
        vm.dstk.dblPush(quot)
    }

    /** `#>` ( d -- c-addr u ) Push address of formatted string */

    fun w_numberSignGreater(vm: ForthVM) {
        val s = pict.toString().reversed()
        var addr = vm.memConfig.scratchStart
        for (c in s) {
            vm.mem[addr++] = c.code
        }
        vm.dstk.dblPop()
        vm.dstk.push(vm.memConfig.scratchStart, s.length)
    }

    /** `#S` ( d1 -- d2 ) Add d1 to pict string (in base) and return 0 */

    fun w_numberSignS(vm: ForthVM) {
        val n = vm.dstk.dblPop()
        pict.append(n.absoluteValue.toString(vm.base).reversed())
        vm.dstk.dblPush(0)
    }

    /** `<#` ( -- ) Start creating pict number */

    fun w_lessNumberSign(@Suppress("unused") vm: ForthVM) {
        pict.clear()
    }

    /** `HOLD` ( n -- ) Add char to pict number */

    fun w_hold(vm: ForthVM) {
        val char = vm.dstk.pop()
        pict.append(char.toChar())
    }

    /** `SIGN` ( n -- ) If num is negative, add "-" to pict string */

    fun w_sign(vm: ForthVM) {
        val n = vm.dstk.pop()
        if (n < 0) pict.append("-")
    }
}