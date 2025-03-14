import kf.primitives.WIfThen
import org.junit.jupiter.api.Test

class WIfThenTest : ForthTestCase() {
    var mod: WIfThen = vm.modulesLoaded["IfThen"]!! as WIfThen

    @Test
    fun w_if() {
        mod.w_if(vm)
        assertDStack(vm.cend - 1)
    }

    @Test
    fun w_else() {
        mod.w_if(vm)
        mod.w_else(vm)
        assertDStack(vm.cend - 1)
    }

    @Test
    fun w_then() {
        assertDStack()
    }

    @Test
    fun integrationTest() {
        eval(": test 10 if 20 else 30 then ; test")
        assertDStack(20)
        eval(": test 0 if 20 else 30 then ; test")
        assertDStack(30)
    }
}