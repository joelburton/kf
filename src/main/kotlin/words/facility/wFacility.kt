package kf.words.facility

import kf.*

object wFacility : IWordClass {
    override val name = "Facility"
    override val description = "Facility words"

    override val words: Array<Word> = arrayOf(
        Word("AT-XY", ::w_notImpl),
        Word("KEY?", ::w_keyQuestion),
        Word("PAGE", ::w_page),
    )

    fun w_keyQuestion(vm: ForthVM) {
        vm.dstk.push(0) // fixme
    }

    /** `page` `( -- : clear screen )` */

    private fun w_page(vm: ForthVM) {
        vm.io.cursor.move {
            setPosition(0, 0)
            clearScreen()
        }
    }
}