import kf.CallableWord
import kf.ForthEOF
import kf.ForthVM
import kf.IOGateway
import kf.WTools
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

val dummyFunc: CallableWord = { it.ip = 2 }

open class ForthTestCase {
    val testIO = IOGateway()
    val vm = ForthVM(io = testIO)

    init {
        vm.verbosity = -2
        vm.reboot()
    }

    fun eval(s: String): String {
        testIO.resetAndLoadCommands(s)
        with(vm) {
            try {
                while (true) {
                    val wn = mem[ip++]
                    dict[wn](this)
                }
            } catch (_: ForthEOF) {
                return testIO.getPrinted()
            }
        }
    }

    fun see(name: String) {
        WTools._see(vm, vm.dict[name], false)
        print((vm.io as IOGateway).getPrinted())
    }

    fun assertDStack(vararg items: Int) {
        assertContentEquals(vm.dstk.asArray(), items)
        vm.dstk.reset()
    }

    fun assertDStackKeep(vararg items: Int) {
        assertContentEquals(vm.dstk.asArray(), items)
    }

    fun assertPrinted(s: String) {
        assertEquals(s, testIO.getPrinted())
    }
}