package kf


fun main(args: Array<String>) {
    val io = IOAnsiConsole()
//    val io = IOTerminal()
    val vm = ForthVM(io = io)
    vm.reboot()
    vm.runVM()
}
