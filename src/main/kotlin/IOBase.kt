package kf

import java.io.BufferedReader
import java.io.InputStreamReader


open class IOBase {
    open val input: BufferedReader =
        BufferedReader(InputStreamReader(System.`in`))
    open val output = System.out
    open val err = System.err
    open val isInteractive = true

    open fun readLine(): String? {
        return input.readLine()
    }

    open fun grey(s: String): String = s
    open fun yellow(s: String): String = s
    open fun red(s: String): String = s
    open fun green(s: String): String = s
    open fun quiet(s: String): String = s
    fun error(s: String) = err.println(red(s))
    fun warn(s: String) = err.println(yellow(s))

    open fun clearScreen() {
        for (i in 1..80) println()
    }
}