package kf

class WDoes(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        Word("create") { _->
            w_create()
        },
        Word("does>", immediate = true, compileOnly = true) { _ -> doesAngle() },
        Word("does") { _-> w_does() },
        Word("addr", compileOnly = true) { _ -> w_addr() },
        Word("addrcall", compileOnly = true) { _ -> w_addrCall() },
    )

    /**  does> : inside of compilation, adds "does" + "ret" */
    fun doesAngle() {
        vm.appendWord("does")
        vm.appendWord("ret")
    }

    /**  does: change most recent word from data to call-fn-with-data-num */
    fun w_does() {
        val w: Word = vm.dict.last
//        w.callable = vm.dict.get("addrcall")!!.callable  FIXME
        w.cpos = vm.cptr + 1
    }

    /**  create: add an empty, new word */
    fun w_create() {
        val name: String = vm.getToken()
        vm.dbg("w_create: %s", name)
        val w: Word = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            callable =  { _ -> w_addr() })
        vm.dict.add(w)
    }

    /**  Used for pure-data things, like "create age 1 allot" (ie variable) */
    fun w_addr() {
        vm.dbg("w_addr: 0x%04x", vm.currentWord.dpos)
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)
    }

    /**  Used for all "does" words (it's the "function" for a constant) */
    fun w_addrCall() {
        vm.dbg("w_addrCall")
        w_addr()
//        w_call(vm)   FIXME
    }
}