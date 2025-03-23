package kf

import com.github.ajalt.mordant.platform.MultiplatformSystem


abstract class InputSource(val id: Int, val path: String) {
    var ptr: Int = 0
    var lineCount: Int = 0
    abstract fun readLineOrNull(): String?
    override fun toString() = "$id:$path:$lineCount"
}

open class FFileSource(id: Int, path: String) : InputSource(id, path) {
    open val content: String =
        MultiplatformSystem.readFileAsUtf8(path).toString()

    override fun readLineOrNull(): String? {
        if (ptr >= content.length) return null
        var s = String()
        while (ptr < content.length && content[ptr++] != '\n') {
            s += content[ptr - 1]
        }
        lineCount += 1
        return s
    }
}

class StdInInputSource() : InputSource(0, "<stdin>") {
    override fun readLineOrNull(): String? {
        lineCount += 1
        return readlnOrNull()
    }
}

class EvalInputSource(override val content: String) : FFileSource(-1, "<eval>")

