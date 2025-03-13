package kf

const val D = true
const val VERSION_STRING = "KPupForth 0.1.0"

@Suppress("KotlinConstantConditions")
val Int.charRepr get() =
    if (this in ' '.code..'~'.code) "'${this.toChar()}'" else ""

fun Int.numToStr(base: Int): String = this.toString(base.coerceIn(2, 36))
