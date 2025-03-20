package primitives

import ForthTestCase
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.time.TimeSource

fun Int.approxEquals(other: Int, tolerance: Double = 0.5): Boolean {
    return abs(this - other) < tolerance
}

class WMiscTest : ForthTestCase() {
    val mod = kf.words.facility.wFacilityExt
    val custom = kf.words.custom.wTimeCustom

    @Test
    fun w_timeAmpDate() {
        mod.w_timeAmpDate(vm, Instant.fromEpochSeconds(0))
        assertDStack(0, 0, 16, 31, 12, 1969)
    }

    @Test
    fun w_ms() {
        vm.dstk.push(50)
        val now = TimeSource.Monotonic.markNow()
        mod.w_ms(vm)
        val duration = now.elapsedNow().inWholeMilliseconds.toInt()
        assertTrue(duration.approxEquals(50, tolerance = 15.0))
    }

    @Test
    fun xw_millis() {
        custom.w_millis(vm)
        val before = vm.dstk.pop()
        Thread.sleep(50)
        custom.w_millis(vm)
        val duration = vm.dstk.pop() - before
        assertTrue(duration.approxEquals(50, tolerance = 15.0))
    }

}