package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


object WMisc : WordClass {
    override val name = "Misc"
    override val primitives: Array<Word> = arrayOf(
        Word("millis", ::w_millis),
        Word("ms", ::w_ms),
        Word("time&date", ::w_timeAmpDate),
    )

    /** `time&date` ( -- secs minutes hours dayOfMonth monthNum year : get now )
     */
    private fun w_timeAmpDate(vm: ForthVM) {
        val now = Clock.System.now()
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
    private fun w_ms(vm: ForthVM) {
        Thread.sleep(vm.dstk.pop().toLong())
    }

    /** `millis` ( -- n : number of milliseconds elapsed since VM started. )
     */
    private fun w_millis(vm: ForthVM) {
        val millis = vm.timeMarkCreated.elapsedNow().inWholeMilliseconds.toInt()
        vm.dstk.push(millis)
    }
}