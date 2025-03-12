import kf.ForthVM
import kf.WInputOutput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WInputOutputTest : ForthTestCase() {
    val mod: WInputOutput

    init {
        mod = vm.modulesLoaded["InputOutput"]!! as WInputOutput
    }

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun w_cr() {
        mod.w_cr()
        assertPrinted("\n")
    }

    @Test
    fun w_emit() {
        vm.dstk.push(65)
        mod.w_emit()
        vm.dstk.push(66)
        mod.w_emit()
        assertPrinted("AB")
    }

    @Test
    fun w_space() {
        mod.w_space()
        assertPrinted(" ")
    }

    @Test
    fun w_nl() {
        mod.w_nl()
        assertDStack(0x0a)
    }

    @Test
    fun w_bl() {
        mod.w_bl()
        assertDStack(0x20)
    }

    // TODO: implement
    @Test
    fun w_key() {
    }

    @Test
    fun w_dot() {
        vm.dstk.push(42)
        mod.w_dot()
        assertPrinted("42 ")
    }

    @Test
    fun w_base() {
        mod.w_base()
        assertDStack(ForthVM.REG_BASE)
    }

    @Test
    fun w_hex() {
        mod.w_hex()
        assertEquals(16, vm.base)
    }

    @Test
    fun w_decimal() {
        mod.w_decimal()
        assertEquals(10, vm.base)
    }

    @Test
    fun w_binary() {
        mod.w_binary()
        assertEquals(2, vm.base)
    }

    @Test
    fun w_octal() {
        mod.w_octal()
        assertEquals(8, vm.base)
    }

    @Test
    fun w_toUpper() {
        vm.dstk.push(97)
        mod.w_toUpper()
        assertDStack(65)
    }

    @Test
    fun w_toLower() {
        vm.dstk.push(65)
        mod.w_toLower()
        assertDStack(97)
    }
}