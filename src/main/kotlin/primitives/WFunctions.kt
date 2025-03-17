package kf.primitives

import kf.D
import kf.ForthVM
import kf.Word
import kf.WordClass

object WFunctions : WordClass {
    override val name = "Functions"

    override val primitives get() = arrayOf(
        Word("call", ::w_call),
        Word("call-by-addr", ::w_callByAddr),
        Word("execute", ::w_execute),
        Word("return", ::w_return ),

        )

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

    /**  `call-by-addr` ( addr -- : call addr )
     */
    fun w_callByAddr(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        vm.rstk.push(vm.ip)
        vm.ip = addr
    }

    /**  `execute` ( n -- : execute a word by word-num )
     */
    fun w_execute(vm: ForthVM) {
        val wn: Int = vm.dstk.pop()
        vm.dict[wn](vm)
    }

    /**  `return` ( r:n -- : return from current word )
     */
    fun w_return(vm: ForthVM) {
        val retAddr = vm.rstk.pop()
        vm.ip = retAddr
    }
}