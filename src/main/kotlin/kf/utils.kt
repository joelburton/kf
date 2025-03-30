@file:OptIn(ExperimentalStdlibApi::class)

package kf

import org.apache.commons.text.WordUtils

/** Set this to true to allow the system to generate copious logging msgs.
 *
 * This isn't changeable except in the source: it's a global constant so that
 * the compiler can optimize away all debugging-level logs if false.
 *
 * Just because this is true doesn't mean you'll *see* the input (change the
 * verbosity of the VM will `some-num r:verbosity !`), but when this is true,
 * doing anything will have a dozen or two checks to see what the verbosity
 * is to decide to log it, and so the whole VM is about 3x slower.
 *
 * Apologies for the absurdly short name; it's in a TON of lines.
 */
const val D = false

const val VERSION_STRING = "KPupForth 0.1.0"


/**  Try a word as an integer:
 *
 * It parses with the provided radix (for safeParseInt("ff", 16) would
 * return 255).
 *
 * Regardless of the given radix, if the string has certain prefixes,
 * it will be interpreted as that radix:
 *
 * 0d12 = 12
 * 0xff = 255
 * 0o10 = 8
 * 0b10 = 2
 * &10 = 10
 * #10  = 10
 * $10  = 16
 * %10  = 2
 * 'A   = 65
 * 'A'  = 65
 *
 * This will throw a ForthError if it can't parse something as a number.
 *
 * While it may be used for other things, this ultimately is what either
 * the evaluator or compiler functions use when it can find a word matching
 * the name. So an error here is a "parsing error", like a user typed this
 * in the interpreter:
 *
 *  >>> 10 20 xxx
 *            ^---- not a word, and not parseable as an int = parsing error
 * */

fun String.toForthInt(base: Int): Int {
    var s = this

    // anywhere a number is parsed, you could also provide a char literal,
    // like `'a'` or `'a`. This isn't standard Forth, but GForth does this,
    // and it's a very nice convenience.

    if ((s[0] == '\'') && (length == 2 || (length == 3 && s[2] == '\''))) {
        return s[1].code
    }

    var radix = base
    if (startsWith("0d")) {
        s = substring(2)
        radix = 10
    } else if (startsWith("0x")) {
        s = substring(2)
        radix = 16
    } else if (startsWith("0b")) {
        s = substring(2)
        radix = 2
    } else if (startsWith("0o")) {
        s = substring(2)
        radix = 8
    } else if (startsWith("%")) {
        s = substring(1)
        radix = 2
    } else if (startsWith("$")) {
        s = substring(1)
        radix = 16
    } else if (startsWith("#")) {
        s = substring(1)
        radix = 10
    } else if (startsWith("&")) {
        s = substring(1)
        radix = 10
    }

    return try {
        s.toInt(radix)
    } catch (_: NumberFormatException) {
        throw ParseError(s)
    }
}

/** Output long hex num, like $0000123a */
val DollarHex = HexFormat { number { prefix = "$" } }

/** Output short-as-can-be, like $123a */
val DollarBriefHex = HexFormat {
    number { prefix = "$"; removeLeadingZeros = true }
}
val Int.hex8 get() = this.toHexString(DollarHex)
val Int.hex get() = this.toHexString(DollarBriefHex)

/** Format an address like $FF99 */
val Int.addr get() = this.toShort().toHexString(DollarHex)

/** Pad integer by 10 --- that can fit $ffffffff, which is the largest num. */
val Int.pad10 get(): String = toString().padStart(10, ' ')

/** Output a string with the char rep like 'A' if # is ASCII range. */

@Suppress("KotlinConstantConditions")
val Int.charRepr get() =
    if (this in ' '.code..'~'.code) "'${this.toChar()}'" else ""

/** Output number as Forth string. */
fun Int.numToStr(base: Int): String = this.toString(base.coerceIn(2, 36))

/** Output number as Forth string with prefixes, like "$AB" or "%1010" */
fun Int.numToStrPrefixed(base: Int) =
     when(base) {
        2 -> "%${toString(2)}"
        10 -> this.toString(10)
        16 -> "$${toString(16)}"
        else -> "$base#${toString(base)}"
    }

// Forth strings come in two forms:
//
// - normal strings, which have an address, and the length is passed separately.
// - counted strings, where the length is the cell before the start of chars.
//
// Many functions, like those in the parser, return a Pair (addr,len): these
// functions can turn those into Kotlin strings.

/** Return string from addr,len pair. */

fun Pair<Int, Int>.strFromAddrLen(vm: ForthVM) =
    CharArray(second) { i -> vm.mem[first + i].toChar() }.concatToString()

/** Return string from len,addr pair. */

fun Pair<Int, Int>.strFromLenAddr(vm: ForthVM) =
    CharArray(first) { i -> vm.mem[second + i].toChar() }.concatToString()

/** Return string from address of counted string. */

fun Int.strFromCSAddr(vm: ForthVM): String {
    // very curious about why that annotation is needed to silence my IDE;
    // without it, it shows a warning.
    @Suppress("KotlinConstantConditions") val len = vm.mem[this]
    return CharArray(len) { i -> vm.mem[this + i + 1].toChar() }.concatToString()
}

/** Wrap words with indent. */

fun String.wrap(maxWidth: Int = 80, indent:Int = 0): String {
    val spaces = " ".repeat(indent)
    return spaces + WordUtils.wrap(
        "$spaces$this", maxWidth - indent, "\n$spaces", false)
}
