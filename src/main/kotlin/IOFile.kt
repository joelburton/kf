package kf

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

/**  Works generally like a console, except input comes from the file.
 */
class IOFile(filePath: String) : IOBase() {
    override val input: BufferedReader
    override val isInteractive = false

    init {
        val inStream = FileInputStream(filePath)
        input = BufferedReader(InputStreamReader(inStream))
    }
}