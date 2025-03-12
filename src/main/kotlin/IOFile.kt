package kf

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

/**  Works generally like a console, except input comes from the file.
 */
class IOFile(path: String) : IOBase() {
    val input: BufferedReader
    override val isInteractive = false

    init {
        input = BufferedReader(InputStreamReader(FileInputStream(path)))
    }

    override fun readLine(): String? = input.readLine()
}