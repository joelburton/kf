package kf

import java.io.BufferedReader
import java.io.File

/**  Works generally like a console, except input comes from the file.
 */
class IOFile(path: String) : IOBase() {
    val input: BufferedReader = File(path).bufferedReader()

    override val isInteractive = false
    override fun readLine(): String? = input.readLine()
}