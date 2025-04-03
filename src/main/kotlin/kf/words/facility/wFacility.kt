package kf.words.facility

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wFacility : IWordModule {
    override val name = "kf.words.facility.wFacility"
    override val description = "Facility words"

    override val words: Array<Word> = arrayOf(
        Word("AT-XY", ::w_atXY),
        Word("KEY?", ::w_keyQuestion),
        Word("PAGE", ::w_page),
    )

    fun w_keyQuestion(vm: IForthVM) {
        vm.dstk.push(vm.io.keyAvail() )
    }

    /** `page` `( -- : clear screen )` */

     fun w_page(vm: IForthVM) {
        vm.io.clearScreen()
    }

    /** `AT-XY` ( n1 n2 -- ) Move cursor to col n1 and row n2 */

    fun w_atXY(vm: IForthVM) {
        val row = vm.dstk.pop()
        val col = vm.dstk.pop()
        vm.io.setXY(col, row)
    }
}