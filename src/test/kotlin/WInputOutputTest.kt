import kf.ForthVM
import kf.primitives.WInputOutput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WInputOutputTest : ForthTestCase() {
    val mod: WInputOutput = vm.modulesLoaded["InputOutput"]!! as WInputOutput

    @Test
    fun w_cr() {
        mod.w_cr(vm)
        assertPrinted("\n")
    }

    @Test
    fun w_emit() {
        vm.dstk.push(65)
        mod.w_emit(vm)
        vm.dstk.push(66)
        mod.w_emit(vm)
        assertPrinted("AB")
    }

    @Test
    fun w_space() {
        mod.w_space(vm)
        assertPrinted(" ")
    }

    @Test
    fun w_nl() {
        mod.w_nl(vm)
        assertDStack(0x0a)
    }

    @Test
    fun w_bl() {
        mod.w_bl(vm)
        assertDStack(0x20)
    }

    // TODO: implement
    @Test
    fun w_key() {
    }

    @Test
    fun w_dot() {
        vm.dstk.push(42)
        mod.w_dot(vm)
        assertPrinted("42 ")
    }

    @Test
    fun w_base() {
        mod.w_base(vm)
        assertDStack(ForthVM.REG_BASE)
    }

    @Test
    fun w_hex() {
        mod.w_hex(vm)
        assertEquals(16, vm.base)
    }

    @Test
    fun w_decimal() {
        mod.w_decimal(vm)
        assertEquals(10, vm.base)
    }

    @Test
    fun w_binary() {
        mod.w_binary(vm)
        assertEquals(2, vm.base)
    }

    @Test
    fun w_octal() {
        mod.w_octal(vm)
        assertEquals(8, vm.base)
    }

    @Test
    fun w_toUpper() {
        vm.dstk.push(97)
        mod.w_toUpper(vm)
        assertDStack(65)
    }

    @Test
    fun w_toLower() {
        vm.dstk.push(65)
        mod.w_toLower(vm)
        assertDStack(97)
    }
}