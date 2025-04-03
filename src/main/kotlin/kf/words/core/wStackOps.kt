package kf.words.core

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wStackOps: IWordModule {
    override val name = "kf.words.core.wStackOps"
    override val description = "Stack operations"

    override val words
        get() = arrayOf<Word>(
            Word("DUP", ::w_dup),
            Word("DROP", ::w_drop),
            Word("?DUP", ::w_questionDup),
            Word("ROT", ::w_rot),
            Word("OVER", ::w_over),
            Word("SWAP", ::w_swap),

            Word("DEPTH", ::w_depth),

            Word("2DROP", ::w_twoDrop),
            Word("2DUP", ::w_twoDup),
            Word("2OVER", ::w_twoOver),
            Word("2SWAP", ::w_twoSwap),
        )

    /**
     * `?DUP` ( x -- 0 | x x ) Duplicate x if it is non-zero */

     fun w_questionDup(vm: IForthVM) {
        val x = vm.dstk.peek()
        if (x != 0) vm.dstk.push(x)
    }

    /** `2DROP` ( x1 x2 -- ) Drop cell pair x1 x2 from the stack */

     fun w_twoDrop(vm: IForthVM) {
        vm.dstk.pop()
        vm.dstk.pop()
    }

    /** `2DUP` ( x1 x2 -- x1 x2 x1 x2 ) Duplicate cell pair x1 x2 */

    fun w_twoDup(vm: IForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(x1, x2, x1, x2)
    }

    /** `2OVER` ( x1 x2 x3 x4 -- x1 x2 x3 x4 x1 x2 ) Copy x1 x2 to top */

     fun w_twoOver(vm: IForthVM) {
        vm.dstk.push(vm.dstk.getFrom(3), vm.dstk.getFrom(2))
    }

    /** `2SWAP` ( x1 x2 x3 x4 -- x3 x4 x1 x2 ) Exchange top two cell pairs */

     fun w_twoSwap(vm: IForthVM) {
        val x4: Int = vm.dstk.pop()
        val x3: Int = vm.dstk.pop()
        val x2: Int = vm.dstk.pop()
        val x1: Int = vm.dstk.pop()
        vm.dstk.push(x3, x4, x1, x2)
    }

    /** `DROP` ( x -- ) Remove x from the stack */

    fun w_drop(vm: IForthVM) {
        vm.dstk.pop()
    }

    /** `DUP` ( x -- x x ) Duplicate x */

    fun w_dup(vm: IForthVM) {
        vm.dstk.push(vm.dstk.peek())
    }

    /** `ROT` ( x1 x2 x3 -- x2 x3 x1 ) Rotate the top three stack entries */

    fun w_rot(vm: IForthVM) {
        vm.dstk.push(vm.dstk.popFrom(2))
    }

    /** `OVER` ( x1 x2 -- x1 x2 x1 ) Place a copy of x1 on top of the stack */

    fun w_over(vm: IForthVM) {
        val x2: Int = vm.dstk.pop()
        val x1: Int = vm.dstk.peek()
        vm.dstk.push(x2, x1)
    }

    /** `SWAP` ( x1 x2 -- x2 x1 ) Exchange the top two stack items */

    fun w_swap(vm: IForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(x2, x1)
    }

    /** `DEPTH` ( -- +n ) +n cell values on data stack */

    fun w_depth(vm: IForthVM) {
        vm.dstk.push(vm.dstk.size)
    }
}