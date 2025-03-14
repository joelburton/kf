@file:Suppress("unused")

package kf

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.Whitespace

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
        Word("callable.") { w_callableDot() },
        Word("foo", staticFunc = WWordsF::foo) { w_callableDot() },
        Word("bar", staticFunc = ::w_bar) { w_callableDot() },
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
        vm.io.println(
            vm.dict.words.joinToString(" ") { it.name },
            whitespace = Whitespace.NORMAL,
            overflowWrap = OverflowWrap.BREAK_WORD
        )
    }

    /**  `.dict` ( -- : list all words with internal info )
     */
    fun w_dotDict() {
        for (i in 0..<vm.dict.size) {
            val w: Word = vm.dict[i]
            vm.io.print(w.getHeaderStr())
        }
        vm.io.println(gray(Word.HEADER_STR))
    }

    /**  `hide` ( in:"name" -- : hides word )
     */
    fun w_hideWord() {
        val name: String = vm.getToken()
        val w: Word = vm.dict[name]
        w.hidden = true
    }


    /**  `unhide` ( in:"name" -- : un-hides word )
     */
    fun w_unhideWord() {
        val name: String = vm.getToken()
        val w: Word = vm.dict[name]
        w.hidden = false
    }

    /**  `unhide-all` ( -- : un-hides all words )
     */
    fun w_unhideAll() {
        for (i in 0..<vm.dict.size) {
            vm.dict[i].hidden = false
        }
    }

    // newname old-name
    /** `synonym` ( in:"new" in:"old" -- : makes new word as alias of old )
     */
    fun w_synonym() {
        val newName: String = vm.getToken()
        val oldName: String = vm.getToken()
        val curWord: Word = vm.dict[oldName]
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
        val w: Word = vm.dict[newName]
        vm.dict.truncateAt(w.wn)
        if (w.cpos != Word.NO_ADDR) vm.cend = w.cpos
    }

    /**  `wn-forget` ( wn -- : delete word and all following words )
     */
    fun w_wnForget() {
        val wn: Int = vm.dstk.pop()
        val w: Word = vm.dict[wn]
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
        val wCall = vm.dict["call"]
        val newName: String = vm.getToken()
        val w = Word(
            newName,
            callable = wCall.callable,
            cpos = vm.cend,
            cposEnd = vm.cend + 4,
            dpos = Word.NO_ADDR
        )
        vm.dict.add(w)
        vm.appendLit(w.wn)
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
        val w: Word = vm.dict[wn]
        vm.io.print(w.name + " ")
    }

    /** `callable.` ( "word" -- : print callable addr ) */

    fun w_callableDot() {
        val token: String = vm.getToken()
        val w: Word = vm.dict[token]
        vm.io.println(w.callable.toString())
        vm.io.println(WWordsF::foo)
    }

    // playing with the idea of switching words to plain funcs

}

object WWordsF {
        fun foo(vm: ForthVM) {
            println("foo /Users/joel/src/kf/src/main/kotlin/WWords.kt")
        }
}

fun w_bar(vm: ForthVM) {
    println("bar https://bar.com/")
}

// 5 ' .   ( n xt )
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