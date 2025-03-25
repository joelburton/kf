package words.core.ext

import ForthTestCase
import kf.strFromAddrLen
import kf.words.core.ext.wStringsExt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class wStringsExtTest : ForthTestCase() {
    val mod = wStringsExt

    @Test
    fun w_cQuote() {
        vm.dend = 0x200
        vm.source.scanner.fill(" hello\"")
        mod.w_cQuote(vm)
        assertDStack(0x200)
    }

    @Test
    fun w_backSlashQuote() {
        vm.dend = 0x200
        vm.source.scanner.fill("a\\nb\"")
        mod.w_sBackSlashQuote(vm)
        assertDStack(0x201, 3)
        assertEquals("a\nb", Pair(0x201, 3).strFromAddrLen(vm))
    }

}