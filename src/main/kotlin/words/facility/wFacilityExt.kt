package kf.words.facility

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object wFacilityExt : IWordClass {
    override val name = "Facility"
    override val description = "Facility words"
    override val words = arrayOf<Word>(
        Word("+FIELD", ::w_notImpl),
        Word("BEGIN-STRUCTURE", ::w_notImpl),
        Word("CFIELD:", ::w_notImpl),
        Word("EKEY", ::w_notImpl),
        Word("EKEY>CHAR", ::w_notImpl),
        Word("EKEY>FKEY", ::w_notImpl),
        Word("EKEY?", ::w_notImpl),
        Word("EMIT?", ::w_notImpl),
        Word("END-STRUCTURE", ::w_notImpl),
        Word("FIELD:", ::w_notImpl),
        Word("K-ALT-MASK", ::w_notImpl),
        Word("K-CTRL-MASK", ::w_notImpl),
        Word("K-DELETE", ::w_notImpl),
        Word("K-DOWN", ::w_notImpl),
        Word("K-END", ::w_notImpl),
        Word("K-F1", ::w_notImpl),
        Word("K-F10", ::w_notImpl),
        Word("K-F11", ::w_notImpl),
        Word("K-F12", ::w_notImpl),
        Word("K-F2", ::w_notImpl),
        Word("K-F3", ::w_notImpl),
        Word("K-F4", ::w_notImpl),
        Word("K-F5", ::w_notImpl),
        Word("K-F6", ::w_notImpl),
        Word("K-F7", ::w_notImpl),
        Word("K-F8", ::w_notImpl),
        Word("K-F9", ::w_notImpl),
        Word("K-HOME", ::w_notImpl),
        Word("K-INSERT", ::w_notImpl),
        Word("K-LEFT", ::w_notImpl),
        Word("K-NEXT", ::w_notImpl),
        Word("K-PRIOR", ::w_notImpl),
        Word("K-RIGHT", ::w_notImpl),
        Word("K-SHIFT-MASK", ::w_notImpl),
        Word("K-UP", ::w_notImpl),
        Word("MS", ::w_ms),
        Word("TIME&DATE", ::w_timeAmpDate),
    )


    /** `time&date` ( -- secs minutes hours dayOfMonth monthNum year : get now )
     */
    fun w_timeAmpDate(vm: ForthVM, now: Instant = Clock.System.now()) {
        val lt = now.toLocalDateTime(TimeZone.currentSystemDefault())
        vm.dstk.push(
            lt.second,
            lt.minute,
            lt.hour,
            lt.dayOfMonth,
            lt.monthNumber,
            lt.year
        )
    }

    /** `ms` ( n -- : pause VM for n milliseconds )
     */
    fun w_ms(vm: ForthVM) {
        Thread.sleep(vm.dstk.pop().toLong())
    }

}