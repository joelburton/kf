package kf.primitives

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.Whitespace
import kf.ForthVM
import kf.Word
import kf.WordClass

object WWords : WordClass {
    override val name = "Words"
    override val primitives get() = arrayOf(
        Word("synonym", ::w_synonym ) ,
        Word("forget", ::w_forget ) ,
        Word("wn-forget", ::w_wnForget ) ,
        Word("marker", ::w_marker ) ,

        Word(".dict", ::w_dotDict ) ,
        Word(".wn-hide", ::w_hideWord ) ,
        Word(".wn-unhide", ::w_unhideWord ) ,
        Word("id.", ::w_wordId ) ,
        Word(".unhide-all", ::w_unhideAll ) ,

        Word("[defined]", ::w_bracketDefined , imm = true) ,
        Word("[undefined]", ::w_bracketUndefined , imm = true) ,
        Word("callable.", ::w_callableDot ) ,
    )

    /**  `\[defined\]` I ( in:"name" -- f : is this word defined? )
     */
    private fun w_bracketDefined(vm: ForthVM) {
        val token: String = vm.interp.getToken()
        val def = vm.dict.getSafe(token) != null
        vm.dstk.push(if (def) ForthVM.Companion.TRUE else ForthVM.Companion.FALSE)
    }

    /**  `\[undefined\]` I ( in:"name" -- f : is this word undefined? )
     */
    private fun w_bracketUndefined(vm: ForthVM) {
        val token: String = vm.interp.getToken()
        val def = vm.dict.getSafe(token) != null
        vm.dstk.push(if (def) ForthVM.Companion.FALSE else ForthVM.Companion.TRUE)
    }


    /**  `.dict` ( -- : list all words with internal info )
     */
    fun w_dotDict(vm: ForthVM) {
        for (i in 0..<vm.dict.size) {
            val w: Word = vm.dict[i]
            vm.io.print(w.getHeaderStr())
        }
        vm.io.println(gray(Word.Companion.HEADER_STR))
    }

    /**  `hide` ( in:"name" -- : hides word )
     */
    fun w_hideWord(vm: ForthVM) {
        val name: String = vm.interp.getToken()
        val w: Word = vm.dict[name]
        w.hidden = true
    }


    /**  `unhide` ( in:"name" -- : un-hides word )
     */
    fun w_unhideWord(vm: ForthVM) {
        val name: String = vm.interp.getToken()
        val w: Word = vm.dict[name]
        w.hidden = false
    }

    /**  `unhide-all` ( -- : un-hides all words )
     */
    fun w_unhideAll(vm: ForthVM) {
        for (i in 0..<vm.dict.size) {
            vm.dict[i].hidden = false
        }
    }

    // newname old-name
    /** `synonym` ( in:"new" in:"old" -- : makes new word as alias of old )
     */
    fun w_synonym(vm: ForthVM) {
        val newName: String = vm.interp.getToken()
        val oldName: String = vm.interp.getToken()
        val curWord: Word = vm.dict[oldName]
        val nw = Word(
            newName,
            fn = curWord.fn,
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
    fun w_forget(vm: ForthVM) {
        val newName: String = vm.interp.getToken()
        val w: Word = vm.dict[newName]
        vm.dict.truncateAt(w.wn)
        if (w.cpos != Word.Companion.NO_ADDR) vm.cend = w.cpos
    }

    /**  `wn-forget` ( wn -- : delete word and all following words )
     */
    fun w_wnForget(vm: ForthVM) {
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
    fun w_marker(vm: ForthVM) {
        val wCall = vm.dict["call"]
        val newName: String = vm.interp.getToken()
        val w = Word(
            newName,
            fn = wCall.fn,
            cpos = vm.cend,
            dpos = Word.Companion.NO_ADDR
        )
        vm.dict.add(w)
        vm.appendLit(w.wn)
        vm.appendWord("wn-forget")
        vm.appendWord(";s")
    }


    /**  `id.` ( wn -- : print name of word )
     */
    fun w_wordId(vm: ForthVM) {
        val wn: Int = vm.dstk.pop()
        val w: Word = vm.dict[wn]
        vm.io.print(w.name + " ")
    }

    /** `callable.` ( "word" -- : print callable addr ) */

    fun w_callableDot(vm: ForthVM) {
        val token: String = vm.interp.getToken()
        val w: Word = vm.dict[token]
        vm.io.println(w.fn.toString())
    }

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