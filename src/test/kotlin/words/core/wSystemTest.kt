package words.core

import ForthTestCase
import kf.words.core.wSystem
import org.junit.jupiter.api.Test

class wSystemTest : ForthTestCase() {
    val mod = wSystem

    @Test
    fun w_environment() {
        vm.scanner.fill("/PAD")
        mod.w_environment(vm)
        assertDStack(vm.memConfig.padEnd - vm.memConfig.padStart)
    }

}