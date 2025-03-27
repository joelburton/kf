package words.core

import EvalForthTestCase
import org.junit.jupiter.api.Test


class wFormattingTestFunc : EvalForthTestCase() {
    @Test
    fun formats() {
        vm.base = 10
        eval("-65535 -1 hex tuck <# # # 'A hold #s # rot sign #> type")
        assertPrinted("-0ffAff")

        vm.base = 10
        eval("65535 0 hex tuck <# # # 'A hold #s # rot sign #> type")
        assertPrinted("0ffAff")
        vm.base = 10
    }
}