import kf.Word
import kf.primitives.WDoes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WDoesTest : ForthTestCase() {
    val mod: WDoes = vm.modulesLoaded["Does"]!! as WDoes

    @Test
    fun w_doesAngle() {
        mod.w_doesAngle(vm)
        assertEquals(vm.dict["does"].wn, vm.mem[vm.cend - 2])
        assertEquals(vm.dict[";s"].wn, vm.mem[vm.cend - 1])
    }

    @Test
    fun w_does() {
        val w = Word("foo", vm.dict["addrcall"].fn)
        vm.dict.add(w)
        vm.currentWord = w
        vm.ip = 0x100
        mod.w_does(vm)
        assertEquals(vm.dict["addrcall"].fn, w.fn)
        assertEquals(0x101, w.cpos)
    }


    @Test
    fun w_addr() {
        vm.mem[0x110] = 42
        val w = Word("foo", vm.dict["addrcall"].fn, dpos=0x110)
        vm.currentWord = w
        mod.w_addr(vm)
        assertDStack(0x110)
    }

    @Test
    fun w_addrCall() {
        vm.mem[0x150] = vm.dict["@"].wn
        vm.mem[0x151] = vm.dict[";s"].wn
        vm.mem[0x200] = 42
        val w = Word(
            "foo",
            cpos=0x150,
            dpos=0x200,
            fn = vm.dict["addrcall"].fn)
        vm.dict.add(w)
        eval("foo")
        assertDStack(42)
    }

    @Test
    fun w_create() {
        eval("create foo")
        assertEquals(vm.dend, vm.dict["foo"].dpos)
    }

    @Test
    fun integrationTestConstant() {
        eval(": const create , does> @ ; 42 const life life")
        assertDStack(42)
    }

    @Test
    fun integrationTestVariable() {
        eval(": variable create , ; 42 variable life life @")
        assertDStack(42)
    }
}