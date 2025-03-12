package kf

class IOAnsiConsole: IOBase() {
    override fun grey(s: String) = "\u001b[0;37m${s}\u001b[0m"
    override fun yellow(s: String) = "\u001b[0;33m${s}\u001b[0m"
    override fun red(s: String) = "\u001b[0;31m${s}\u001b[0m"
    override fun green(s: String) = "\u001b[0;92m${s}\u001b[0m"
    override fun quiet(s: String) = "\u001b[0;37m${s}\u001b[0m"

    override fun clearScreen() {
        o.print("\u001b[H\u001b[2J")
        o.flush()
    }
}
