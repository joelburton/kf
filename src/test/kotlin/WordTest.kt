import kf.CallableWord
import kf.ForthVM
import kf.Word
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class WordTest {

    @Test
    fun companion() {
        assertEquals(0xffff, Word.NO_ADDR)
    }

    @Test
    fun testToString() {
        assertEquals(
            "test",
            Word("test", callable = dummyFunc).toString()
        )
    }

    @Test
    fun invoke() {
        val vm = ForthVM()
        val word = Word("test", callable = dummyFunc)
        word.invoke(vm)
        assertEquals(2, vm.ip)
    }
}