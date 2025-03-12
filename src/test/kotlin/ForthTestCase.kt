import kf.ForthEOF
import kf.ForthVM
import kf.IOGateway
import kf.WTools
import org.junit.jupiter.api.Assertions.assertEquals

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
        assertEquals(items.size, vm.dstk.size)
        for (v in items.reversed()) assertEquals(v, vm.dstk.pop())
    }
}