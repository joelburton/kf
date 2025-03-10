package kf

fun main() {
    val io = IOAnsiConsole()
    val vm = ForthVM(io=io)
    vm.reboot()
    vm.runVM()
}