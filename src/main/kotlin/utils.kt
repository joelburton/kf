@file:OptIn(ExperimentalStdlibApi::class)

package kf

import kotlin.text.HexFormat

const val D = true
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
 *
 * This will throw a ForthError. */
fun String.toForthInt(radix: Int): Int {
    var s = this

    // anywhere a number is parsed, you could also provide a char literal,
    // like `'a'` or `'a`. This isn't standard Forth, but GForth does this,
    // and it's a very nice convenience.

    if (s.isCharLit) {
        return s[1].code
    }

    var _radix = radix
    if (startsWith("0d")) {
        s = substring(2)
        _radix = 10
    } else if (startsWith("0x")) {
        s = substring(2)
        _radix = 16
    } else if (startsWith("0b")) {
        s = substring(2)
        _radix = 2
    } else if (startsWith("0o")) {
        s = substring(2)
        _radix = 8
    } else if (startsWith("%")) {
        s = substring(1)
        _radix = 2
    } else if (startsWith("$")) {
        s = substring(1)
        _radix = 16
    } else if (startsWith("#")) {
        s = substring(1)
        _radix = 10
    } else if (startsWith("&")) {
        s = substring(1)
        _radix = 10
    }

    return try {
        s.toInt(_radix)
    } catch (e: NumberFormatException) {
        throw ParseError(s)
    }
}

val DollarHex = HexFormat { number { prefix = "$" } }
val DollarBriefHex = HexFormat {
    number { prefix = "$"; removeLeadingZeros = true }
}
val Int.hex8 get() = this.toHexString(DollarHex)
val Int.hex get() = this.toHexString(DollarBriefHex)
val Int.addr get() = this.toShort().toHexString(DollarHex)
val Int.pad10 get(): String = toString().padStart(10, ' ')

@Suppress("KotlinConstantConditions")
val Int.charRepr get() =
    if (this in ' '.code..'~'.code) "'${this.toChar()}'" else ""

fun Int.numToStr(base: Int): String = this.toString(base.coerceIn(2, 36))
fun Int.numToStrPrefixed(base: Int) =
     when(base) {
        2 -> "%${toString(2)}"
        10 -> this.toString(10)
        16 -> "$${toString(16)}"
        else -> "$base#${toString(base)}"
    }



val String.isCharLit get() = (get(0) == '\'')
        && (length == 2 || (length == 3 && get(2) == '\''))

/** Return string from addr,len pair. */

fun Pair<Int, Int>.strFromAddrLen(vm: ForthVM) =
    CharArray(second) { i -> vm.mem[first + i].toChar() }.concatToString()

fun Pair<Int, Int>.strFromLenAddr(vm: ForthVM) =
    CharArray(first) { i -> vm.mem[second + i].toChar() }.concatToString()

/** Return string from address of counted string. */

fun Int.strFromCSAddr(vm: ForthVM): String {
    @Suppress("KotlinConstantConditions") val len = vm.mem[this]
    return CharArray(len) { i -> vm.mem[this + i + 1].toChar() }.concatToString()
}
