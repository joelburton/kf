import kf.ForthVM
import kf.IOGateway
import kf.WMathLogic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WMathLogicTest {
    val vm = ForthVM(io=IOGateway())
     val math = WMathLogic(vm)

@Test
 fun w_and() {
    vm.dstk.push(0x0101, 0x0100)
    math.w_and()
    assertEquals(1, vm.dstk.size )
    assertEquals(0x0100, vm.dstk.pop() )
 }

@Test
 fun w_or() {
    vm.dstk.push(0x0101, 0x0100)
    math.w_or()
    assertEquals(1, vm.dstk.size )
    assertEquals(0x0101, vm.dstk.pop() )
 }

@Test
 fun w_not() {}

@Test
 fun w_xor() {}

@Test
 fun w_inv() {}

@Test
 fun w_add() {}

@Test
 fun w_sub() {}

@Test
 fun w_mul() {}

@Test
 fun w_div() {}

@Test
 fun w_mod() {}

@Test
 fun w_eq() {}

@Test
 fun w_gt() {}

@Test
 fun w_lt() {}

@Test
 fun w_gte() {}

@Test
 fun w_lte() {}

@Test
 fun w_inc() {}

@Test
 fun w_dec() {}

@Test
 fun w_ne() {}

@Test
 fun w_eq0() {}

@Test
 fun w_sqrt() {}

@Test
 fun w_true() {}

@Test
 fun w_false() {}
}