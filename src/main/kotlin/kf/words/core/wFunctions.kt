package kf.words.core

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule


object wFunctions: IWordModule {
    override val name = "kf.words.core.wFunctions"
    override val description = "Handling calling and exiting custom functions"

    override val words = arrayOf<Word>(
        Word("EXECUTE", ::w_execute),
        Word("EXIT", ::w_exit),
    )

    /** `EXECUTE` ( i * x xt -- j * x ) Execute word with wn=xt
     *
     *   `' dup execute` should be the same as `dup`
     *
     **/

    fun w_execute(vm: IForthVM) {
        val wn = vm.dstk.pop()
        val w = vm.dict[wn]
        vm.currentWord = w
        w.fn(vm)
    }

    /** EXIT ( -- ) ( R: nest-sys -- ) Return from call
     *
     * Before executing EXIT within a do-loop, a program shall discard the
     * loop-control parameters by executing UNLOOP.
     *
     * There is an alias of this, `;S`, which exists to help the decompiler
     * figure out when a function is returning mid-function or the end of the
     * function definition.
     */

    fun w_exit(vm: IForthVM) {
        vm.ip = vm.rstk.pop()
    }

    /** `CALL` ( --  ) call currentWord
     *
     * This is the function that colon-definitions get (as opposed to
     * primitives, where their function is an actual Kotlin func.
     *
     * This isn't registered as a word --- just as a function. It wouldn't
     * be possible to define this in Forth, since it relies on the Kotlin-only
     * "currentWord" data.
     *
     **/

    fun w_call(vm: IForthVM) {
        vm.rstk.push(vm.ip)
        vm.ip = vm.currentWord.cpos
    }
}
