package kf.words.custom

import kf.ForthVM
import kf.dict.Word
import kf.interfaces.FALSE
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.interfaces.TRUE
import kotlin.math.sqrt

object wLogicCustom : IWordModule {
    override val name = "kf.words.custom.wLogicCustom"
    override val description = "Logic Extra"
    override val words get() = arrayOf<Word>(
        Word("NOT", ::w_not ) ,
        Word(">=", ::w_gte ) ,
        Word("<=", ::w_lte ) ,
        Word("SQRT", ::w_sqrt ) ,
    )

    /**  ( n1 -- ~n1 : bitwise NOT ) */
    fun w_not(vm: IForthVM) {
        vm.dstk.push(vm.dstk.pop().inv() and ForthVM.Companion.MAX_INT)
    }

    /**  ( n1 n2 -- n1>=n2? : greater-than-or-equal ) */
    fun w_gte(vm: IForthVM) {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b >= a) TRUE else FALSE)
    }

    /**  ( n1 n2 -- n1<=n2? : less-than-or-equal ) */
    fun w_lte(vm: IForthVM) {
        val a = vm.dstk.pop()
        val b = vm.dstk.pop()
        vm.dstk.push(if (b <= a) TRUE else FALSE)
    }



    /**  ( n -- int(sqrt(n)) : square root ) */
    fun w_sqrt(vm: IForthVM) {
        val res = sqrt(vm.dstk.pop().toDouble())
        vm.dstk.push(res.toInt())
    }

}