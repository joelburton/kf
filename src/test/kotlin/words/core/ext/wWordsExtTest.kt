package words.core.ext

import EvalForthTestCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class wWordsExtTest : EvalForthTestCase() {
    @Test
    fun marker() {
        eval(": a 1 ;")
        eval("marker foo")
        eval(": a 2 ; a")
        assertDStack(2)
        eval("foo a")
        assertDStack(1)
    }

}