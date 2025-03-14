package kf

class WDoes(val vm: ForthVM) : WordClass {
    override val name = "Does"

    override val primitives: Array<Word> = arrayOf(
        Word("does>", imm = true, compO = true) {  doesAngle() },
        Word("does") { w_does() },
        Word("addr", compO = true) {  w_addr() },
        Word("addrcall", compO = true) {  w_addrCall() },
        Word("create") { w_create() },
    )

    /**  does> : inside of compilation, adds "does" + "ret"
     *
     * Example:
     *
     *      : const create , does> @ ;
     *      42 const life
     *  then:
     *      life   => 42
     *
     *  `does>` modifies the definition of `const`` at the time of compiling it.
     *
     * */

    fun doesAngle() {
        vm.appendWord("does")
        vm.appendWord("return")
    }

    /**  does: change most recent word from data to call-fn-with-data-num */

    fun w_does() {
        val w: Word = vm.dict.last
        w.callable = vm.dict["addrcall"].callable
        w.cpos = vm.ip + 1
    }

    /**  Used for pure-data things, like "create age 1 allot" (ie variable) */

    fun w_addr() {
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)
    }

    /**`addrcall` ( -- addr : w/currWord: push dpos, then call it )'
     *
     * Used for all "does" words (it's the "function" for a constant). This
     * word will need to have both a dpos and a cpos, and only words
     * made by create + does will have that.
     * */

    fun w_addrCall() {
        // push addr
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)

        // call
        vm.rstk.push(vm.ip)
        vm.ip = vm.currentWord.cpos
    }

    /**  create: add an empty, new word */

    fun w_create() {
        val name: String = vm.getToken()
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            callable =  {  w_addr() })
        vm.dict.add(w)
    }
}
