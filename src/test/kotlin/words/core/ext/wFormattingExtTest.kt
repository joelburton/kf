package words.core.ext

import EvalForthTestCase
import org.junit.jupiter.api.Test

class wFormattingExtFuncTest : EvalForthTestCase() {
    @Test
    fun formats() {
        eval("1234 0 tuck <# # # s\" ---\" holds # # #> type")
        assertPrinted("12---34")
    }

}