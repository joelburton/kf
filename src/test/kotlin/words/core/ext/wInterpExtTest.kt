package words.core.ext

import ForthTestCase
import kf.sources.SourceTestEvalInput
import kf.words.core.ext.wInterpExt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wInterpExtTest : ForthTestCase() {
    val mod = wInterpExt

    @Test
    fun w_parseName() {
        vm.source.scanner.fill(" abc 123 ")
        mod.w_parseName(vm)
        assertDStack(vm.source.scanner.start + 1, 3)
    }

    @Test
    fun w_refill() {
        vm.sources.clear()
        vm.sources.add(SourceTestEvalInput(vm, "abc 123"))
        vm.source.scanner.nextLine()
        mod.w_refill(vm)
        assertEquals("abc 123", vm.source.scanner.toString())
    }
}