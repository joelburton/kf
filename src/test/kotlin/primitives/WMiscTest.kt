//package primitives
//
//import ForthTestCase
//import kf.primitives.WMisc
//import kotlinx.datetime.Instant
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import kotlin.math.abs
//import kotlin.time.TimeSource
//
//fun Int.approxEquals(other: Int, tolerance: Double = 0.5): Boolean {
//    return abs(this - other) < tolerance
//}
//
//class WMiscTest : ForthTestCase() {
//    @Test
//    fun w_timeAmpDate() {
//        WMisc.w_timeAmpDate(vm, Instant.fromEpochSeconds(0))
//        assertDStack(0, 0, 16, 31, 12, 1969)
//    }
//
//    @Test
//    fun w_ms() {
//        vm.dstk.push(50)
//        val now = TimeSource.Monotonic.markNow()
//        WMisc.w_ms(vm)
//        val duration = now.elapsedNow().inWholeMilliseconds.toInt()
//        assertTrue(duration.approxEquals(50, tolerance = 15.0))
//    }
//
//    @Test
//    fun w_millis() {
//        WMisc.w_millis(vm)
//        val before = vm.dstk.pop()
//        Thread.sleep(50)
//        WMisc.w_millis(vm)
//        val duration = vm.dstk.pop() - before
//        assertTrue(duration.approxEquals(50, tolerance = 15.0))
//    }
//
//}