import kf.ForthVM
import kf.IOGateway
import kf.WMathLogic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WMathLogicTest {
    val vm = ForthVM(io = IOGateway())
    val math = WMathLogic(vm)

    fun rez() : Int {
        assertEquals(1, vm.dstk.size)
        return vm.dstk.pop()
    }

    @Test
    fun w_and() {
        vm.dstk.push(0b0101, 0b0100)
        math.w_and()
        assertEquals(0b0100, rez())
    }

    @Test
    fun w_or() {
        vm.dstk.push(0b0101, 0b0100)
        math.w_or()
        assertEquals(0b0101, rez())
    }

    @Test
    fun w_not() {
        vm.dstk.push(0b0101)
        math.w_not()
        assertEquals(0b1111111111111111111111111111010, rez())
    }

    @Test
    fun w_xor() {
        vm.dstk.push(0b0101, 0b0100)
        math.w_xor()
        assertEquals(0b0001, rez())
    }

    @Test
    fun w_negate() {
        vm.dstk.push(5)
        math.w_negate()
        assertEquals(-5, rez())

        vm.dstk.push(-5)
        math.w_negate()
        assertEquals(5, rez())
    }

    @Test
    fun w_inv() {
        vm.dstk.push(0b0101)
        math.w_inv()
        assertEquals(0b1111111111111111111111111111010, rez())
    }

    @Test
    fun w_add() {
        vm.dstk.push(10, 20)
        math.w_add()
        assertEquals(30, rez())
    }

    @Test
    fun w_sub() {
        vm.dstk.push(10, 20)
        math.w_sub()
        assertEquals(-10, rez())
    }

    @Test
    fun w_mul() {
        vm.dstk.push(10, 20)
        math.w_mul()
        assertEquals(200, rez())
    }

    @Test
    fun w_div() {
        vm.dstk.push(10, 2)
        math.w_div()
        assertEquals(5, rez())
    }

    @Test
    fun w_mod() {
        vm.dstk.push(10, 2)
        math.w_mod()
        assertEquals(0, rez())

        vm.dstk.push(11, 2)
        math.w_mod()
        assertEquals(1, rez())
    }

    @Test
    fun w_eq() {
        vm.dstk.push(10, 20)
        math.w_eq()
        assertEquals(WMathLogic.FALSE, rez())

        vm.dstk.push(10, 10)
        math.w_eq()
        assertEquals(WMathLogic.TRUE, rez())
    }

    @Test
    fun w_gt() {
        vm.dstk.push(10, 20)
        math.w_gt()
        assertEquals(WMathLogic.FALSE, rez())

        vm.dstk.push(10, 10)
        math.w_gt()
        assertEquals(WMathLogic.FALSE, rez())

        vm.dstk.push(20, 10)
        math.w_gt()
        assertEquals(WMathLogic.TRUE, rez())
    }

    @Test
    fun w_lt() {
        vm.dstk.push(10, 20)
        math.w_lt()
        assertEquals(WMathLogic.TRUE, rez())

        vm.dstk.push(10, 10)
        math.w_lt()
        assertEquals(WMathLogic.FALSE, rez())

        vm.dstk.push(20, 10)
        math.w_lt()
        assertEquals(WMathLogic.FALSE, rez())
    }

    @Test
    fun w_gte() {
        vm.dstk.push(10, 20)
        math.w_gte()
        assertEquals(WMathLogic.FALSE, rez())

        vm.dstk.push(10, 10)
        math.w_gte()
        assertEquals(WMathLogic.TRUE, rez())

        vm.dstk.push(20, 10)
        math.w_gte()
        assertEquals(WMathLogic.TRUE, rez())
    }

    @Test
    fun w_lte() {
        vm.dstk.push(10, 20)
        math.w_lte()
        assertEquals(WMathLogic.TRUE, rez())

        vm.dstk.push(10, 10)
        math.w_lte()
        assertEquals(WMathLogic.TRUE, rez())

        vm.dstk.push(20, 10)
        math.w_lte()
        assertEquals(WMathLogic.FALSE, rez())

    }

    @Test
    fun w_inc() {
        vm.dstk.push(10)
        math.w_inc()
        assertEquals(11, rez())
    }

    @Test
    fun w_dec() {
        vm.dstk.push(10)
        math.w_dec()
        assertEquals(9, rez())
    }

    @Test
    fun w_ne() {
        vm.dstk.push(10, 20)
        math.w_ne()
        assertEquals(WMathLogic.TRUE, rez())

        vm.dstk.push(10, 10)
        math.w_ne()
        assertEquals(WMathLogic.FALSE, rez())
    }

    @Test
    fun w_eq0() {
        vm.dstk.push(0)
        math.w_eq0()
        assertEquals(WMathLogic.TRUE, rez())

        vm.dstk.push(-1)
        math.w_eq0()
        assertEquals(WMathLogic.FALSE, rez())

        vm.dstk.push(42)
        math.w_eq0()
        assertEquals(WMathLogic.FALSE, rez())
    }

    @Test
    fun w_sqrt() {
        vm.dstk.push(16)
        math.w_sqrt()
        assertEquals(4, rez())

        vm.dstk.push(17)
        math.w_sqrt()
        assertEquals(4, rez())
    }

    @Test
    fun w_true() {
        math.w_true()
        assertEquals(WMathLogic.TRUE, rez())
    }

    @Test
    fun w_false() {
        math.w_false()
        assertEquals(WMathLogic.FALSE, rez())
    }
}