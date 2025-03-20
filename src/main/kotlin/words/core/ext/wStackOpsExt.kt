package kf.words.core.ext

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wStackOpsExt: IWordClass {
    override val name = "core.ext.stackOpsExt"
    override val description = "Stack operations"

    override val words
        get() = arrayOf(
            Word("NIP", ::w_nip ) ,
            Word("PICK", ::w_pick ) ,
            Word("TUCK", ::w_tuck ) ,
            Word("ROLL", ::w_roll ) ,
        )



    // roll -- 2 roll is rot, 1 roll is swap, etc   3 a b c d e => c d e a b
    /**  `roll` ( for:n=3 a b c d e n -- a c d e b : move nth-from-top to top )
     *
     * - 2 roll is same as rot:  a b c 2 -- b c a
     * - 1 roll is swap:         a b 1   -- b a
     * - 0 roll is no-op:        a b 0   -- a b
     */
    private fun w_roll(vm: ForthVM) {
        vm.dstk.push(vm.dstk.popFrom(vm.dstk.pop()))
    }

    /**  `pick` ( for:n=3 a b c d n -- a b c d b : copy nth-from-top to top )
     */
    private fun w_pick(vm: ForthVM) {
        vm.dstk.push(vm.dstk.getFrom(vm.dstk.pop()))
    }



    /** `nip` ( n1 n2 -- n2 : drop second item from stack )
     */
    fun w_nip(vm: ForthVM) {
        val a: Int = vm.dstk.pop()
        vm.dstk.pop()
        vm.dstk.push(a)
    }


    /**  `tuck` ( a b -- b a b : copy top item to behind second )
     */
    private fun w_tuck(vm: ForthVM) {
        val n2: Int = vm.dstk.pop()
        val n1: Int = vm.dstk.pop()
        vm.dstk.push(n2, n1, n2)
    }

}