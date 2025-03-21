//import org.junit.jupiter.api.Test
//
//class WIfThenTest : EvalForthTestCase() {
//    override var mod = kf.words.core.wIfThen
//
//    @Test
//    fun w_if() {
//        mod.w_if(vm)
//        assertRStack(vm.cend - 1)
//    }
//
//    @Test
//    fun w_else() {
//        mod.w_if(vm)
//        mod.w_else(vm)
//        assertRStack(vm.cend - 1)
//    }
//
//    @Test
//    fun w_then() {
//        assertRStack()
//    }
//
//    @Test
//    fun integrationTest() {
//        eval(": test 10 if 20 else 30 then ; test")
//        assertDStack(20)
//        eval(": test 0 if 20 else 30 then ; test")
//        assertDStack(30)
//    }
//}