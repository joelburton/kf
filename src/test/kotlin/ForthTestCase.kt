import kf.ForthEOF
import kf.ForthVM
import kf.IOGateway
import kf.WTools
import org.junit.jupiter.api.Assertions.assertEquals

open class ForthTestCase {
    val io = IOGateway()
    val vm = ForthVM(io = io)

    init {
        vm.verbosity = -2
        vm.reboot()
    }

    fun eval(s: String): String {
        io.resetAndLoadCommands(s)
        try {
            while (true) {
                val wn = vm.mem[vm.ip++]
                vm.dict.get(wn).exec(vm)
            }
        } catch (e: ForthEOF) {
            return io.getPrinted()
        }
    }

    fun see(name: String) {
        WTools._see(vm, vm.dict.get(name), false)
        print((vm.io as IOGateway).getPrinted())
    }

    fun assertDStack(vararg items: Int) {
        assertEquals(items.size, vm.dstk.size)
        for (v in items.reversed()) assertEquals(v, vm.dstk.pop())
    }
}