package kf.words.facility

import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word

object wFacility : IWordModule {
    override val name = "kf.words.facility.wFacility"
    override val description = "Facility words"

    override val words: Array<Word> = arrayOf(
        Word("AT-XY", ::w_atXY),
        Word("KEY?", ::w_keyQuestion),
        Word("PAGE", ::w_page),
    )

    fun w_keyQuestion(vm: ForthVM) {
        vm.dstk.push(vm.io.keyAvail() )
    }

    /** `page` `( -- : clear screen )` */

     fun w_page(vm: ForthVM) {
        vm.io.clearScreen()
    }

    /** `AT-XY` ( n1 n2 -- ) Move cursor to col n1 and row n2 */

    fun w_atXY(vm:ForthVM) {
        val row = vm.dstk.pop()
        val col = vm.dstk.pop()
        vm.io.setXY(col, row)
    }
}