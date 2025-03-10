import kf.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith


class DictTest {
    val d: Dict = Dict(ForthVM(), capacity = 3)

    @Test
    fun reset() {
        val word = Word("word", callable = dummyFunc)
        d.add(word)
        d.currentlyDefining = word
        assertEquals(1, d.size)
        d.reset()
        assertEquals(0, d.size)
        assertNull(d.currentlyDefining)
    }

    @Test
    fun size() {
        val word = Word("word", callable = dummyFunc)
        d.add(word)
        assertEquals(1, d.size)
    }

    @Test
    fun last() {
        val word = Word("word", callable = dummyFunc)
        d.add(word)
        assertEquals(word, d.last)
    }

    @Test
    fun get() {
        val word = Word("word", callable = dummyFunc)
        d.add(word)
        assertFailsWith<WordNotFoundException> { d.get("no-word") }
        assertEquals(word, d.get("word"))
    }

    @Test
    fun testGet() {
        val word = Word("word", callable = dummyFunc)
        d.add(word)
        assertFailsWith<WordNotFoundException> { d.get(-1) }
        assertEquals(word, d.get(0))
    }

    @Test
    fun getNum() {
        val word = Word("word", callable = dummyFunc)
        d.add(word)
        assertFailsWith<WordNotFoundException> { d.get("no-word") }
        assertEquals(0, d.getNum("word"))
    }

    @Test
    fun addMany() {
        d.addMany(
            arrayOf(
                Word("word1", callable = dummyFunc),
                Word("word2", callable = dummyFunc),
            ),
            "dummy"
        )
        assertEquals(2, d.size)
    }

    @Test
    fun dictFull() {
        val tooMany = arrayOf(
            Word("word1", callable = dummyFunc),
            Word("word2", callable = dummyFunc),
            Word("word3", callable = dummyFunc),
            Word("word4", callable = dummyFunc),
        )
        assertFailsWith<DictFullError> { d.addMany(tooMany, "dummy") }
    }

    @Test
    fun truncate() {
        d.addMany(
            arrayOf(
                Word("word1", callable = dummyFunc),
                Word("word2", callable = dummyFunc)
            ),
            "dummy"
        )
        d.truncateAt(1)
        assertEquals(1, d.size)
        d.truncateAt(10) // not an error to truncate more than we have
        assertEquals(1, d.size)
    }

    @Test
    fun removeLast() {
        d.addMany(
            arrayOf(
                Word("word1", callable = dummyFunc),
                Word("word2", callable = dummyFunc)
            ),
            name = "dummy"
        )
        d.removeLast()
        assertEquals(1, d.size)
    }
}
