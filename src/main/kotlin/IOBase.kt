package kf


open class IOBase {
    open val o = System.out
    open val err = System.err
    open val isInteractive = true

    open fun grey(s: String): String = s
    open fun yellow(s: String): String = s
    open fun red(s: String): String = s
    open fun green(s: String): String = s
    open fun quiet(s: String): String = s

    open fun readLine() = kotlin.io.readLine()
    fun error(s: String) = err.println(red(s))
    fun warn(s: String) = err.println(yellow(s))

    open fun clearScreen() {
        for (i in 1..80) o.println()
    }
}