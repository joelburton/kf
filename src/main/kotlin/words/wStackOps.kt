package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wStackOps: IWordClass {
    override val name = "StackOps"
    override val description = "Stack operations"

    override val words
        get() = arrayOf(
            Word("?DUP", ::w_questionDup),
            Word("2DROP", ::w_2drop),
            Word("2DUP", ::w_2dup),
            Word("2OVER", ::w_2over),
            Word("2SWAP", ::w_2swap),
            Word("DROP", ::w_drop),
            Word("DUP", ::w_dup),
            Word("ROT", ::w_rot),
            Word("OVER", ::w_over),
            Word("SWAP", ::w_swap),
            Word("DEPTH", ::w_depth),
        )

    /**
     * ?DUP
     * question-dupe
     * CORE
     * ( x -- 0 | x x )
     * Duplicate x if it is non-zero.
     */

    private fun w_questionDup(vm: ForthVM) {
        val x = vm.dstk.peek()
        if (x != 0) vm.dstk.push(x)
    }

    /** 2DROP   two-drop   CORE
     *
     * ( x1 x2 -- )
     *
     * Drop cell pair x1 x2 from the stack.
     */

    private fun w_2drop(vm: ForthVM) {
        vm.dstk.pop()
        vm.dstk.pop()
    }

    /** 2DUP     two-dupe    CORE
     *
     * ( x1 x2 -- x1 x2 x1 x2 )
     *
     * Duplicate cell pair x1 x2.
     */

    fun w_2dup(vm: ForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(x1, x2, x1, x2)
    }

    /** 2OVER    two-over    CORE
     *
     * ( x1 x2 x3 x4 -- x1 x2 x3 x4 x1 x2 )
     *
     * Copy cell pair x1 x2 to the top of the stack.
     */

    private fun w_2over(vm: ForthVM) {
        vm.dstk.push(vm.dstk.getFrom(3), vm.dstk.getFrom(2))
    }


    /**  2SWAP   two-swap    CORE
     *
     * ( x1 x2 x3 x4 -- x3 x4 x1 x2 )
     *
     * Exchange the top two cell pairs.
     */

    private fun w_2swap(vm: ForthVM) {
        val x4: Int = vm.dstk.pop()
        val x3: Int = vm.dstk.pop()
        val x2: Int = vm.dstk.pop()
        val x1: Int = vm.dstk.pop()
        vm.dstk.push(x3, x4, x1, x2)
    }

    /** DROP     CORE
     *
     * ( x -- )
     *
     * Remove x from the stack.
     */

    fun w_drop(vm: ForthVM) {
        vm.dstk.pop()
    }


    /** DUP  dupe    CORE
     *
     * ( x -- x x )
     *
     * Duplicate x.
     */

    fun w_dup(vm: ForthVM) {
        vm.dstk.push(vm.dstk.peek())
    }

    /** ROT     rote     CORE
     *
     * ( x1 x2 x3 -- x2 x3 x1 )
     *
     * Rotate the top three stack entries.
     */

    fun w_rot(vm: ForthVM) {
        vm.dstk.push(vm.dstk.popFrom(2))
    }

    /** OVER     CORE
     *
     * ( x1 x2 -- x1 x2 x1 )
     *
     * Place a copy of x1 on top of the stack.
     */

    fun w_over(vm: ForthVM) {
        val x2: Int = vm.dstk.pop()
        val x1: Int = vm.dstk.peek()
        vm.dstk.push(x2, x1)
    }


    /** SWAP     CORE
     *
     * ( x1 x2 -- x2 x1 )
     *
     * Exchange the top two stack items.
     */

    fun w_swap(vm: ForthVM) {
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.dstk.push(x2, x1)
    }

    /** DEPTH    CORE
     *
     * ( -- +n )
     *
     * +n is the number of single-cell values contained in the data stack before +n was placed on the stack.
     */

    fun w_depth(vm: ForthVM) {
        vm.dstk.push(vm.dstk.size)
    }
}