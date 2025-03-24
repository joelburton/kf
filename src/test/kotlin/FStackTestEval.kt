import kf.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FStackTestEval : ForthTestCase() {
    var stk = FStack(vm, "myStack", 100, 103)

    @Test
    fun asArray() {
        stk.push(1)
        stk.push(2 )
        assertEquals("myStack [1, 2]", stk.toString())
    }

    @Test
    fun sp() {
        assertEquals(104, stk.sp)
        stk.push(1)
        assertEquals(103, stk.sp)
        stk.pop()
        assertEquals(104, stk.sp)
    }

    @Test
    fun pushPop() {
        stk.push(1)
        stk.push(2)
        assertEquals(2, stk.pop())
        assertEquals(1, stk.pop())
        stk.push(3, 4)
        assertEquals(4, stk.pop())
        assertEquals(3, stk.pop())
        stk.push(5, 6, 7)
        assertEquals(7, stk.pop())
        assertEquals(6, stk.pop())
        assertEquals(5, stk.pop())
        stk.push(10, 20)
        stk.push(10, 20)
        assertFailsWith<StackOverflowError> { stk.push(10, 20) }
        assertFailsWith<StackOverflowError> { stk.push(8, 9, 10, 11, 12) }
    }

    @Test
    fun underflow() {
        stk.push(1)
        assertEquals(1, stk.pop())
        assertFailsWith<StackUnderflowError> { stk.pop() }
    }

    @Test
    fun overflow() {
        stk.push(1)
        stk.push(1)
        stk.push(1)
        stk.push(1)
        assertFailsWith<StackOverflowError> { stk.push(0) }
    }

    @Test
    fun getAt() {
        stk.push(10)
        stk.push(20)
        assertEquals(10, stk.getAt(0))
        assertEquals(20, stk.getAt(1))
        assertFailsWith<StackPtrInvalidError> { stk.getAt(2) }
        assertFailsWith<StackPtrInvalidError> { stk.getAt(-1) }
    }

    @Test
    fun getFrom() {
        stk.push(10)
        stk.push(20)
        assertEquals(20, stk.getFrom(0))
        assertEquals(10, stk.getFrom(1))
        assertFailsWith<StackPtrInvalidError> { stk.getFrom(2) }
        assertFailsWith<StackPtrInvalidError> { stk.getFrom(-1) }
    }

    @Test
    fun size() {
        assertEquals(0, stk.size)
        stk.push(1)
        assertEquals(1, stk.size)
    }

    @Test
    fun peek() {
        stk.push(1)
        assertEquals(1, stk.peek())
        stk.push(2)
        assertEquals(2, stk.peek())
        stk.pop()
        stk.pop()
    }

    @Test
    fun reset() {
        stk.push(1)
        stk.push(2)
        stk.reset()
        assertEquals(104, stk.sp)
    }

    @Test
    fun simpleDump() {
        stk.push(1)
        stk.push(2)
        vm.base = 10
        stk.simpleDump()
        assertEquals("<2> 1 2 ", getOutput())
    }

    @Test
    fun dump() {
        stk.push(1)
        stk.push(255)
        stk.push(65)
        stk.push(2)
        stk.dump()
        assertEquals(
            """
myStack[0] = $00000001 (         1)
myStack[1] = $000000ff (       255)
myStack[2] = $00000041 (        65) 'A'
myStack[3] = $00000002 (         2)   <- top

        """.trimIndent(), getOutput())
    }

    @Test
    fun popFrom() {
        stk.reset()
        stk.push(1, 2, 3)
        assertEquals(3, stk.popFrom(0))
        assertEquals("myStack [1, 2]", stk.toString())

        stk.reset()
        stk.push(1, 2, 3)
        assertEquals(2, stk.popFrom(1))
        assertEquals("myStack [1, 3]", stk.toString())

        stk.reset()
        stk.push(1, 2, 3)
        assertEquals(1, stk.popFrom(2))
        assertEquals("myStack [2, 3]", stk.toString())

        stk.reset()
        stk.push(1, 2, 3)
        assertFailsWith<StackPtrInvalidError> { stk.popFrom(-1) }
        assertFailsWith<StackPtrInvalidError> { stk.popFrom(3) }
    }
}