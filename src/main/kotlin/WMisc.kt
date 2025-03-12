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

    // `time&date'
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

    private fun w_ms() {
        try {
            Thread.sleep(vm.dstk.pop().toLong(), 0)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    /** Number of milliseconds elapsed since VM was started.
     */
    private fun w_millis() {
        val millis = vm.timeMarkCreated.elapsedNow().inWholeMilliseconds.toInt()
        vm.dstk.push(millis)
    }
}