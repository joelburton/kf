package words.core.ext

import ForthTestCase
import kf.words.core.ext.wParseExt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wParseExtTest : ForthTestCase() {
    val mod = wParseExt

    @Test
    fun w_parse() {
        vm.source.scanner.fill(" hellox")
        vm.dstk.push('x'.code)
        mod.w_parse(vm)
        assertDStack(vm.source.scanner.start, 6)
        assertEquals(" hello", vm.source.scanner.curToken())
    }

}