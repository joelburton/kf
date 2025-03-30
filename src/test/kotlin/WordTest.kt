import kf.ForthVM
import kf.consoles.RecordingConsole
import kf.dict.Word
import kf.interps.InterpBase
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
            Word("test", ::dummyFn).toString()
        )
    }

    @Test
    fun invoke() {
        val vm = ForthVM(
            io = RecordingConsole(),
            interp = InterpBase(),
        )
        val word = Word("test", ::dummyFn)
        word.invoke(vm)
        assertEquals(2, vm.ip)
    }
}