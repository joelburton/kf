package dict

import ForthTestCase
import dummyFn
import kf.*
import kf.consoles.RecordingConsole
import kf.dict.Dict
import kf.dict.DictFullError
import kf.dict.IWordModule
import kf.dict.Word
import kf.dict.WordNotFoundError
import kf.interps.InterpBase
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith



class DictTest  : ForthTestCase() {
    object FakeMod: IWordModule {
        override val name = "dict.FakeMod"
        override val description = "Fake"
        override val words = arrayOf<Word>(
            Word("word1", fn = ::dummyFn),
            Word("word2", fn = ::dummyFn),
        )
    }

    val dict: Dict = Dict(
        ForthVM(RecordingConsole(), InterpBase()), capacity = 3)

    @Test
    fun reset() {
        val word = Word("word", fn = ::dummyFn)
        dict.add(word)
        dict.currentlyDefining = word
        assertEquals(1, dict.size)
        dict.reset()
        assertEquals(0, dict.size)
        assertNull(dict.currentlyDefining)
    }

    @Test
    fun size() {
        val word = Word("word", fn = ::dummyFn)
        dict.add(word)
        assertEquals(1, dict.size)
    }

    @Test
    fun last() {
        val word = Word("word", fn = ::dummyFn)
        dict.add(word)
        assertEquals(word, dict.last)
    }

    @Test
    fun get() {
        val word = Word("word", fn = ::dummyFn)
        dict.add(word)
        assertFailsWith<WordNotFoundError> { dict["no-word"] }
        assertEquals(word, dict["word"])
    }

    @Test
    fun testGet() {
        val word = Word("word", fn = ::dummyFn)
        dict.add(word)
        assertFailsWith<WordNotFoundError> { dict[-1] }
        assertEquals(word, dict[0])
    }

    @Test
    fun getNum() {
        val word = Word("word", fn = ::dummyFn)
        dict.add(word)
        assertFailsWith<WordNotFoundError> { dict["no-word"] }
        assertEquals(0, dict["word"].wn)
    }

    @Test
    fun addModule() {
        dict.addModule(FakeMod)
        assertEquals(2, dict.size)
    }

    @Test
    fun dictFull() {
        val w = Word("word1", fn = ::dummyFn)
        dict.add(w)
        dict.add(w)
        dict.add(w)
        assertFailsWith<DictFullError> { dict.add(w) }
    }

    @Test
    fun truncate() {
        val w = Word("word1", fn = ::dummyFn)
        dict.add(w)
        dict.add(w)
        dict.add(w)
        dict.truncateAt(1)
        assertEquals(1, dict.size)
        dict.truncateAt(10) // not an error to truncate more than we have
        assertEquals(1, dict.size)
    }

    @Test
    fun removeLast() {
        val w = Word("word1", fn = ::dummyFn)
        dict.add(w)
        dict.add(w)
        dict.add(w)
        dict.removeLast()
        assertEquals(2, dict.size)
    }
}
