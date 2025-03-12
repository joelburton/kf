package kf

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


class WMisc(val vm: ForthVM): WordClass {
    override val name = "Misc"
    override val primitives: Array<Word> = arrayOf(
        Word("millis") { w_millis() },
        Word("ms") { w_ms() },
        Word("time&date") { w_timeAmpDate() },
        )

    /** `time&date` ( -- secs minutes hours dayOfMonth monthNum year : get now )
     */
    private fun w_timeAmpDate() {
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
    private fun w_ms() {
        Thread.sleep(vm.dstk.pop().toLong())
    }

    /** `millis` ( -- n : number of milliseconds elapsed since VM started. )
     */
    private fun w_millis() {
        val millis = vm.timeMarkCreated.elapsedNow().inWholeMilliseconds.toInt()
        vm.dstk.push(millis)
    }
}