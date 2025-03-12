package kf

class WWords(val vm: ForthVM) : WordClass {
    override val name = "Words"
    override val primitives: Array<Word> = arrayOf(
        Word("words") { w_words() },
        Word("synonym") { w_synonym() },
        Word("forget") { w_forget() },
        Word("wn-forget") { w_wnForget() },
        Word("marker") { w_marker() },
        Word("'") { w_tick() },

        Word(".dict") { w_dotDict() },
        Word(".wn-hide") { w_hideWord() },
        Word(".wn-unhide") { w_unhideWord() },
        Word("id.") { w_wordId() },
        Word(".unhide-all") { w_unhideAll() },

        Word("[defined]", imm = true) { w_bracketDefined() },
        Word("[undefined]", imm = true) { w_bracketUndefined() },
    )

    /**  `\[defined\]` I ( in:"name" -- f : is this word defined? )
     */
    private fun w_bracketDefined() {
        val token: String = vm.getToken()
        val def = vm.dict.getSafe(token) != null
        vm.dstk.push(if (def) ForthVM.TRUE else ForthVM.FALSE)
    }

    /**  `\[undefined\]` I ( in:"name" -- f : is this word undefined? )
     */
    private fun w_bracketUndefined() {
        val token: String = vm.getToken()
        val def = vm.dict.getSafe(token) != null
        vm.dstk.push(if (def) ForthVM.FALSE else ForthVM.TRUE)
    }

    /**  `words` ( -- :dump words )
     */
    fun w_words() {
        var currLineLen = 0
        val width: Int = vm.termWidth

        for (i in 0..<vm.dict.size) {
            val w: Word = vm.dict.get(i)
            if (w.hidden) continue

            val s: String = w.name + " "
            currLineLen += s.length
            if (currLineLen > width) {
                vm.io.output.println()
                currLineLen = s.length
            }
            vm.io.output.print(s)
        }
        vm.io.output.println()
    }

    /**  `.dict` ( -- : list all words with internal info )
     */
    fun w_dotDict() {
        for (i in 0..<vm.dict.size) {
            val w: Word = vm.dict.get(i)
            vm.io.output.print(w.getHeaderStr(vm.io))
        }
        vm.io.output.println(vm.io.grey(Word.HEADER_STR))
    }

    /**  `hide` ( in:"name" -- : hides word )
     */
    fun w_hideWord() {
        val name: String = vm.getToken()
        val w: Word = vm.dict.get(name)
        w.hidden = true
    }


    /**  `unhide` ( in:"name" -- : un-hides word )
     */
    fun w_unhideWord() {
        val name: String = vm.getToken()
        val w: Word = vm.dict.get(name)
        w.hidden = false
    }

    /**  `unhide-all` ( -- : un-hides all words )
     */
    fun w_unhideAll() {
        for (i in 0..<vm.dict.size) {
            vm.dict.get(i).hidden = false
        }
    }

    // newname old-name
    /** `synonym` ( in:"new" in:"old" -- : makes new word as alias of old )
     */
    fun w_synonym() {
        val newName: String = vm.getToken()
        val oldName: String = vm.getToken()
        val curWord: Word = vm.dict.get(oldName)
        val nw = Word(
            newName,
            callable = curWord.callable,
            cpos = curWord.cpos,
            dpos = curWord.dpos,
            compO = curWord.compO,
            imm = curWord.imm,
            interpO = curWord.interpO,
        )
        vm.dict.add(nw)
    }

    /**  `forget` ( in:"name" -- : delete word and all following words )
     */
    fun w_forget() {
        val newName: String = vm.getToken()
        val w: Word = vm.dict.get(newName)
        vm.dict.truncateAt(w.wn)
        if (w.cpos != Word.NO_ADDR) vm.cend = w.cpos
    }

    /**  `wn-forget` ( wn -- : delete word and all following words )
     */
    fun w_wnForget() {
        val wn: Int = vm.dstk.pop()
        val w: Word = vm.dict.get(wn)
        vm.dict.truncateAt(wn)
        vm.cend = w.cpos
    }

    /**  `marker` IO ( in:"name" -- : create word which will forget itself )
     *
     * For example:
     *
     * : aa ;
     * marker foo
     * : bb ;
     * foo
     *
     * Will forget back to (and including) when the "foo" marker was made.
     */
    fun w_marker() {
        val wCall = vm.dict.get("call")
        val newName: String = vm.getToken()
        val w = Word(
            newName,
            callable = wCall.callable,
            cpos = vm.cend,
            dpos = Word.NO_ADDR
        )
        vm.dict.add(w)
        vm.appendLit(w.wn!!)
        vm.appendWord("wn-forget")
        vm.appendWord("return")
    }

    /**  `'` ( in:"name" -- wn : get wn for name and push to stack )
     *
     * Example: ' dup => pushes wn-of-dup to dstk
     */
    fun w_tick() {
        val token: String = vm.getToken()
        val wn: Int = vm.dict.getNum(token)
        vm.dstk.push(wn)
    }

    /**  `id.` ( wn -- : print name of word )
     */
    fun w_wordId() {
        val wn: Int = vm.dstk.pop()
        val w: Word = vm.dict.get(wn)
        vm.io.output.print(w.name + " ")
    }
} // 5 ' .   ( n xt )
//execute ( )      \ execute the xt of .
//\ does not work as intended:
//\ : foo ' . ;
//\ 5 foo execute
//\ instead:
//: foo ['] . ;
//5 foo execute    \ execute the xt of .
//\ Usage of ' in colon definition:
//: bar ' execute ;
//5 bar .          \ execute the xt of .