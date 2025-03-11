package kf

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