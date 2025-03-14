import kf.WDoes
import kf.Word
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WDoesTest : ForthTestCase() {
    val mod: WDoes

    init {
        mod = vm.modulesLoaded["Does"]!! as WDoes
    }

    @BeforeEach
    fun setUp() {
//        TODO("Not yet implemented")
    }

    @Test
    fun doesAngle() {
        mod.doesAngle()
        assertEquals(vm.dict["does"].wn, vm.mem[vm.cend - 2])
        assertEquals(vm.dict["return"].wn, vm.mem[vm.cend - 1])
    }

    @Test
    fun w_does() {
        val w = Word("foo", callable = vm.dict["addrcall"].callable)
        vm.dict.add(w)
        vm.currentWord = w
        vm.ip = 0x100
        mod.w_does()
        assertEquals(vm.dict["addrcall"].callable, w.callable)
        assertEquals(0x101, w.cpos)
    }


    @Test
    fun w_addr() {
        vm.mem[0x110] = 42
        val w = Word("foo", dpos=0x110, callable = vm.dict["addrcall"].callable)
        vm.currentWord = w
        mod.w_addr()
        assertDStack(0x110)
    }

    @Test
    fun w_addrCall() {
        vm.mem[0x150] = vm.dict["@"].wn
        vm.mem[0x151] = vm.dict["return"].wn
        vm.mem[0x200] = 42
        val w = Word(
            "foo",
            cpos=0x150,
            dpos=0x200,
            callable = vm.dict["addrcall"].callable)
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