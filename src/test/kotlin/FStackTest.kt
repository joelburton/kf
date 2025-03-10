import kf.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FStackTest {
    val io = IOGateway()
    val vm = ForthVM(io=io)
    var s = FStack(vm, "myStack", 0, 3)

    @Test
    fun asArray() {
        s.push(1)
        s.push(2 )
        assertEquals("myStack [1, 2]", s.toString())
    }

    @Test
    fun sp() {
        assertEquals(4, s.sp)
        s.push(1)
        assertEquals(3, s.sp)
        s.pop()
        assertEquals(4, s.sp)
    }

    @Test
    fun pushPop() {
        s.push(1)
        s.push(2)
        assertEquals(2, s.pop())
        assertEquals(1, s.pop())
        s.push(3, 4)
        assertEquals(4, s.pop())
        assertEquals(3, s.pop())
        s.push(5, 6, 7)
        assertEquals(7, s.pop())
        assertEquals(6, s.pop())
        assertEquals(5, s.pop())
        s.push(10, 20)
        s.push(10, 20)
        assertFailsWith<StackOverflow> { s.push(10, 20) }
        assertFailsWith<StackOverflow> { s.push(8, 9, 10, 11, 12) }
    }

    @Test
    fun underflow() {
        s.push(1)
        assertEquals(1, s.pop())
        assertFailsWith<StackUnderflow> { s.pop() }
    }

    @Test
    fun overflow() {
        s.push(1)
        s.push(1)
        s.push(1)
        s.push(1)
        assertFailsWith<StackOverflow> { s.push(0) }
    }

    @Test
    fun getAt() {
        s.push(10)
        s.push(20)
        assertEquals(10, s.getAt(0))
        assertEquals(20, s.getAt(1))
        assertFailsWith<StackInvalid> { s.getAt(2) }
        assertFailsWith<StackInvalid> { s.getAt(-1) }
    }

    @Test
    fun getFrom() {
        s.push(10)
        s.push(20)
        assertEquals(20, s.getFrom(0))
        assertEquals(10, s.getFrom(1))
        assertFailsWith<StackInvalid> { s.getFrom(2) }
        assertFailsWith<StackInvalid> { s.getFrom(-1) }
    }

    @Test
    fun size() {
        assertEquals(0, s.size)
        s.push(1)
        assertEquals(1, s.size)
    }

    @Test
    fun peek() {
        s.push(1)
        assertEquals(1, s.peek())
        s.push(2)
        assertEquals(2, s.peek())
        s.pop()
        s.pop()
    }

    @Test
    fun reset() {
        s.push(1)
        s.push(2)
        s.reset()
        assertEquals(4, s.sp)
    }

    @Test
    fun simpleDump() {
        s.push(1)
        s.push(2)
        s.simpleDump()
        assertEquals("<2> 1 2", io.getPrinted())
    }

    @Test
    fun dump() {
        s.push(1)
        s.push(255)
        s.push(65)
        s.push(2)
        s.dump()
        assertEquals("""
myStack[0] = 0x00000001 (   1)
myStack[1] = 0x000000ff ( 255)
myStack[2] = 0x00000041 (  65) 'A'
myStack[3] = 0x00000002 (   2) <- top

        """.trimIndent(), io.getPrinted())
    }

    @Test
    fun popFrom() {
        s.reset()
        s.push(1, 2, 3)
        assertEquals(3, s.popFrom(0))
        assertEquals("myStack [1, 2]", s.toString())

        s.reset()
        s.push(1, 2, 3)
        assertEquals(2, s.popFrom(1))
        assertEquals("myStack [1, 3]", s.toString())

        s.reset()
        s.push(1, 2, 3)
        assertEquals(1, s.popFrom(2))
        assertEquals("myStack [2, 3]", s.toString())

        s.reset()
        s.push(1, 2, 3)
        assertFailsWith<StackInvalid> { s.popFrom(-1) }
        assertFailsWith<StackInvalid> { s.popFrom(3) }
    }
}