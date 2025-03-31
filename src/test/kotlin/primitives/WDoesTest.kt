package primitives//import kf.Word
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//
//class WDoesTest : EvalForthTestCase() {
//    val mod = kf.words.core.wCreate
//
//    init {
//        vm.dict.addModule(kf.words.custom.wCreateCustom)
//    }
//
//    @Test
//    fun w_addr() {
//        vm.mem[0x110] = 42
//        val w = Word("foo", vm.dict["addrcall"].fn, dpos=0x110)
//        vm.currentWord = w
//        wCreateCustom.w_addr(vm)
//        assertDStack(0x110)
//    }
//
//    @Test
//    fun w_addrCall() {
//        vm.mem[0x150] = vm.dict["@"].wn
//        vm.mem[0x151] = vm.dict[";s"].wn
//        vm.mem[0x200] = 42
//        val w = Word(
//            "foo",
//            cpos=0x150,
//            dpos=0x200,
//            fn = vm.dict["addrcall"].fn)
//        vm.dict.add(w)
//        eval("foo")
//        assertDStack(42)
//    }
//
//}