package kf.words.core

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl


object wFunctions: IWordClass {
    override val name = "Functions"
    override val description = "Handling calling and exiting custom functions"

    override val words = arrayOf(
        Word("EXECUTE", ::w_execute),
        Word("EXIT", ::w_exit),
    )



    /**
     * EXECUTE     CORE
     *
     * ( i * x xt -- j * x )
     *
     * Remove xt from the stack and perform the semantics identified by it.
     * Other stack effects are due to the word EXECUTEd.
     */

    fun w_execute(vm: ForthVM) {
        val wn = vm.dstk.pop()
        vm.dict[wn](vm)
    }

    /** EXIT     CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Execution:
     *      ( -- ) ( R: nest-sys -- )
     *
     * Return control to the calling definition specified by nest-sys. Before
     * executing EXIT within a do-loop, a program shall discard the loop-control
     * parameters by executing UNLOOP.
     *
     */

    fun w_exit(vm: ForthVM) {
        vm.ip = vm.rstk.pop()
    }



// Not a word -- just a func

    /** `call` ( -- : call word in current_word )
     *
     * The `current_word` stuff feels like a kludge; perhaps this should
     * be on the stack? Or at least, in a register? Then we wouldn't need
     * duplicative stuff like w_callByAddr, since this also calls by addr ---
     * they just get the addr from different places.
     */
    fun w_call(vm: ForthVM) {
        vm.rstk.push(vm.ip)
        vm.ip = vm.currentWord.cpos
    }


}


