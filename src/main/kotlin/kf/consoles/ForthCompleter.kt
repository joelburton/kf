package kf.consoles

import kf.dict.Word
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

/** JLine completer that uses the Forth dictionary to complete. */

internal class ForthCompleter(val words: List<Word>) : Completer {
    override fun complete(
        reader: LineReader,
        line: ParsedLine,
        candidates: MutableList<Candidate>
    ) {
        for (word in words) candidates.add(Candidate(word.name))
    }
}
