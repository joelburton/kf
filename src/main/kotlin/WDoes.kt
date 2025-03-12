package kf

class WDoes(val vm: ForthVM) : WordClass {
    override val name = "Does"

    override val primitives: Array<Word> = arrayOf(
        Word("create") { _-> w_create() },
        Word("does>", imm = true, compO = true) {  doesAngle() },
        Word("does") { _-> w_does() },
        Word("addr", compO = true) {  w_addr() },
        Word("addrcall", compO = true) {  w_addrCall() },
    )

    /**  does> : inside of compilation, adds "does" + "ret" */
    fun doesAngle() {
        vm.appendWord("does")
        vm.appendWord("ret")
    }

    /**  does: change most recent word from data to call-fn-with-data-num */
    fun w_does() {
        val w: Word = vm.dict.last
        w.callable = vm.dict.get("addrcall").callable
        w.cpos = vm.ip + 1
    }

    /**  create: add an empty, new word */
    fun w_create() {
        val name: String = vm.getToken()
        val w: Word = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            callable =  {  w_addr() })
        vm.dict.add(w)
    }

    /**  Used for pure-data things, like "create age 1 allot" (ie variable) */
    fun w_addr() {
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)
    }

    /**  Used for all "does" words (it's the "function" for a constant) */
    fun w_addrCall() {
        // push addr
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)

        // call
        vm.rstk.push(vm.ip)
        vm.ip = vm.currentWord.cpos
    }
}