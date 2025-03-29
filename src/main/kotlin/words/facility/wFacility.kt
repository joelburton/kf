package kf.words.facility

import kf.*
import org.jline.utils.InfoCmp.Capability

object wFacility : IWordModule {
    override val name = "kf.words.facility.wFacility"
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
        vm.io.terminal?.let { term ->
            term.puts(Capability.clear_screen);
            term.flush();
        } ?: vm.io.println("\n\n\n\n\n\n\n\n")
    }
}