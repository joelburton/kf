package kf

class WFunctions(val vm: ForthVM) : WordClass {
    override val name = "Functions"

    override val primitives: Array<Word> = arrayOf(
        Word("call") { _ -> w_call() },
        Word("call-by-addr") { _ -> w_callByAddr() },
        Word("execute") { _ -> w_execute() },
        Word("return") { _ -> w_return() },

        )

    /** `call` ( -- : call word in current_word )
     *
     * The `current_word` stuff feels like a kludge; perhaps this should
     * be on the stack? Or at least, in a register? Then we wouldn't need
     * duplicative stuff like w_callByAddr, since this also calls by addr ---
     * they just get the addr from different places.
     */
    fun w_call() {
        vm.rstk.push(vm.ip)
        vm.ip = vm.currentWord.cpos
    }

    /**  `call-by-addr` ( addr -- : call addr )
     */
    fun w_callByAddr() {
        val addr: Int = vm.dstk.pop()
        vm.rstk.push(vm.ip)
        vm.ip = addr
    }

    /**  `execute` ( n -- : execute a word by word-num )
     */
    fun w_execute() {
        val wn: Int = vm.dstk.pop()
        vm.dict.get(wn).exec(vm)
    }

    /**  `return` ( r:n -- : return from current word )
     */
    fun w_return() {
        val retAddr = vm.rstk.pop()
        vm.ip = retAddr
    }
}