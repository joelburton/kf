package kf

import java.util.*
import java.util.concurrent.TimeUnit

class WMisc(val vm: ForthVM): WordClass {
    override val name = "Misc"
    override val primitives: Array<Word> = arrayOf(
        Word("millis") { _ -> w_millis() },
        Word("ms") { _ -> w_ms() },
        Word("time&date") { _ -> w_timeAmpDate() },
        )

    // `time&date'
    private fun w_timeAmpDate() {
        val now = Calendar.getInstance()
        vm.dstk.push(
            now[Calendar.SECOND],
            now[Calendar.MINUTE],
            now[Calendar.HOUR_OF_DAY],
            now[Calendar.DAY_OF_MONTH],
            now[Calendar.MONTH] + 1,
            now[Calendar.YEAR]
        )
    }

    private fun w_ms() {
        try {
            Thread.sleep(vm.dstk.pop().toLong(), 0)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    private fun w_millis() {
        val c = System.currentTimeMillis()
        val millis = (c and 0x7fffffffL).toInt()
        vm.dstk.push(millis)
    }
}