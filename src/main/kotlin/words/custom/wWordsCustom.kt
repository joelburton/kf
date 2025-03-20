package kf.words.custom

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wWordsCustom : IWordClass {
    override val name = "WordsExtra"
    override val description = "Words Extension"
    override val words get() = arrayOf(
        Word("WN-FORGET", ::w_wnForget ) ,
        Word(".WN-HIDE", ::w_hideWord ) ,
        Word(".WN-UNHIDE", ::w_unhideWord ) ,
        Word("ID.", ::w_IDDot ) ,
        Word(".UNHIDE-ALL", ::w_unhideAll ) ,
        Word("CALLABLE.", ::w_callableDot ) ,
    )


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



    /**  `wn-forget` ( wn -- : delete word and all following words )
     */
    fun w_wnForget(vm: ForthVM) {
        val wn: Int = vm.dstk.pop()
        val w: Word = vm.dict[wn]
        vm.dict.truncateAt(wn)
        vm.cend = w.cpos
    }


    /**  `id.` ( wn -- : print name of word )
     */
    fun w_IDDot(vm: ForthVM) {
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
