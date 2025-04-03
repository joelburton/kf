package kf.words.custom

import kf.dict.Dict
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.strFromAddrLen

object wWordsCustom : IWordModule {
    override val name = "kf.words.custom.wWordsCustom"
    override val description = "Words Extension"
    override val words get() = arrayOf<Word>(
        Word(".WN-FORGET", ::w_wnForget ) ,
        Word(".WN-HIDE", ::w_hideWord ) ,
        Word(".WN-UNHIDE", ::w_unhideWord ) ,
        Word("ID.", ::w_IDDot ) ,
        Word(".UNHIDE-ALL", ::w_unhideAll ) ,
        Word("CALLABLE.", ::w_callableDot ) ,
    )


    /**  `hide` ( in:"name" -- : hides word )
     */
    fun w_hideWord(vm: IForthVM) {
        val name: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = vm.dict[name]
        w.hidden = true
    }


    /**  `unhide` ( in:"name" -- : un-hides word )
     */
    fun w_unhideWord(vm: IForthVM) {
        val name: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = vm.dict[name]
        w.hidden = false
    }

    /**  `unhide-all` ( -- : un-hides all words )
     */
    fun w_unhideAll(vm: IForthVM) {
        for (i in 0..<vm.dict.size) {
            vm.dict[i].hidden = false
        }
    }



    /**  `wn-forget` ( wn -- : delete word and all following words )
     */
    fun w_wnForget(vm: IForthVM) {
        val wn: Int = vm.dstk.pop()
        val w = vm.dict[wn]
        vm.dict.truncateAt(wn)
        vm.cend = w.cpos
    }


    /**  `id.` ( wn -- : print name of word )
     */
    fun w_IDDot(vm: IForthVM) {
        val wn: Int = vm.dstk.pop()
        val w = vm.dict[wn]
        vm.io.print(w.name + " ")
    }

    /** `callable.` ( "word" -- : print callable addr ) */

    fun w_callableDot(vm: IForthVM) {
        val token: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = vm.dict[token]
        vm.io.println(w.fn.toString())
    }

}
