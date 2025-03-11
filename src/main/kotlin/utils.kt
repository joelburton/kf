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
fun tryAsInt(s: String, radix: Int): Int {
    var s = s
    var radix = radix
    if (s.startsWith("0d")) {
        s = s.substring(2)
        radix = 10
    } else if (s.startsWith("0x")) {
        s = s.substring(2)
        radix = 16
    } else if (s.startsWith("0b")) {
        s = s.substring(2)
        radix = 2
    } else if (s.startsWith("0o")) {
        s = s.substring(2)
        radix = 8
    } else if (s.startsWith("%")) {
        s = s.substring(1)
        radix = 2
    } else if (s.startsWith("$")) {
        s = s.substring(1)
        radix = 16
    } else if (s.startsWith("#")) {
        s = s.substring(1)
        radix = 10
    } else if (s.startsWith("&")) {
        s = s.substring(1)
        radix = 10
    }

    return try {
        s.toInt(radix)
    } catch (e: NumberFormatException) {
        throw ParseError(s)
    }
}