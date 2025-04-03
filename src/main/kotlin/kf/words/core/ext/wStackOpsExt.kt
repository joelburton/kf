package kf.words.core.ext

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wStackOpsExt : IWordModule {
    override val name = "kf.words.core.ext.stackOpsExt"
    override val description = "Stack operations"

    override val words: Array<Word>
        get() = arrayOf(
            Word("NIP", ::w_nip),
            Word("PICK", ::w_pick),
            Word("TUCK", ::w_tuck),
            Word("ROLL", ::w_roll),
        )

    /**  `ROLL` ( for:n=3 a b c d e n -- a c d e b : move nth-from-top to top )
     *
     * - 2 roll is same as rot:  a b c 2 -- b c a
     * - 1 roll is swap:         a b 1   -- b a
     * - 0 roll is no-op:        a b 0   -- a b
     */

    fun w_roll(vm: IForthVM) {
        vm.dstk.push(vm.dstk.popFrom(vm.dstk.pop()))
    }

    /**  `PICK` ( for:n=3 a b c d n -- a b c d b : copy nth-from-top to top ) */

    fun w_pick(vm: IForthVM) {
        vm.dstk.push(vm.dstk.getFrom(vm.dstk.pop()))
    }

    /** `NIP` ( n1 n2 -- n2 : drop second item from stack ) */

    fun w_nip(vm: IForthVM) {
        val a: Int = vm.dstk.pop()
        vm.dstk.pop()
        vm.dstk.push(a)
    }

    /**  `tuck` ( a b -- b a b : copy top item to behind second ) */
    fun w_tuck(vm: IForthVM) {
        val n2: Int = vm.dstk.pop()
        val n1: Int = vm.dstk.pop()
        vm.dstk.push(n2, n1, n2)
    }

}