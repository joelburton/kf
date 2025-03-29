package kf.consoles

import kf.ForthVM
import kf.ParseError
import kf.toForthInt
import org.jline.reader.Highlighter
import org.jline.reader.LineReader
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle.DEFAULT
import org.jline.utils.AttributedStyle.GREEN
import java.util.regex.Pattern


/** Highlighter for Forth, compatible with JLine interface.
 *
 * Overall works well. Some limitations:
 *
 * - parses numbers as such (in blue), using current base, but if you change
 *   base in a line, it won't recognize that: `10 hex aa` won't show aa as num.
 *
 * - can't recognize a word defined on this line, so ": fn 10 ; fn"
 *   won't bold the second "fn", to show it as a recognized word
 *
 * - it assumes that any known word ending with a quote is a string-starting
 *   word. This is right for ." s" c" abort" , but if a new word is made, it
 *   will be assumed as such. Of course, it's pretty Forth-standard that
 *   foo" should be a string-starting word, so this feels ok.
 *
 * */

internal class ForthHighlighter(val vm: ForthVM) : Highlighter {
    val token = "(\\s+)|(\\\\\\s+.*)|(\\S+)".toRegex()

    override fun highlight(
        reader: org.jline.reader.LineReader, buffer: String
    ): AttributedString {
        val base = vm.base
        var inComment = false
        var inString = false
        val sb = AttributedStringBuilder()

        for (m in token.findAll(buffer)) {
            val ws = m.groups[1]?.value
            val backSlashComment = m.groups[2]?.value
            val word = m.groups[3]?.value

            // whitespace is just whitespace
            if (ws != null) sb.append(ws)

            // backslash comments are grey
            else if (backSlashComment != null)
                sb.append(backSlashComment, DEFAULT.foreground(249))

            // for words:
            // - recognize ( comments ) and make them grey
            // - guess that any word ending with quote (." s" etc) starts string
            // - strings are green until a closing string
            // - if word is in dict: bold
            // - if not: try as number; if so: bold
            // - else: either typo or a word currently being defined: underline

            else if (word != null) {
                if (inString && word.endsWith("\"")) {
                    sb.append(word, DEFAULT.foreground(GREEN))
                    inString = false
                } else if (inString) {
                    sb.append(word, DEFAULT.foreground(GREEN))
                } else if (inComment && word.endsWith(")")) {
                    sb.append(word, DEFAULT.foreground(249))
                    inComment = false
                } else if (inComment) {
                    sb.append(word, DEFAULT.foreground(249))
                } else if (word == "(") {
                    sb.append("(", DEFAULT.foreground(249))
                    inComment = true
                } else if (vm.dict.getSafe(word) != null) {
                    sb.append(word, DEFAULT.bold()) // recognized word
                    if (word.endsWith("\"")) inString = true // ." s" etc
                } else {
                    try {
                        word.toForthInt(base)
                        sb.append(word, DEFAULT.foreground(195).bold()) // num
                    } catch (_: ParseError) {
                        sb.append(word, DEFAULT.underline()) // unknown
                    }
                }
            } else throw Exception("Critical failure for highlighter: $buffer")
        }
        return sb.toAttributedString()
    }

    override fun refresh(reader: LineReader?) = super.refresh(reader)

    // not really sure these would be called

    override fun setErrorPattern(p0: Pattern?) =
        throw Exception("Not yet implemented: setErrorPattern $p0")

    override fun setErrorIndex(p0: Int) =
        throw Exception("Not yet implemented: setErrorIndex $p0")
}
