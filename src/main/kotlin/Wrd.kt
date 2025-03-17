package kf

data class Wrd(
    val name: String,
    val comp: StaticFunc?,
    val inter: StaticFunc?
) {
}

fun w_myLongishFunction(vm: ForthVM) {}

val a = Wrd("dup", ::w_myLongishFunction, ::w_myLongishFunction)
val b = Wrd("dup", null, ::w_myLongishFunction)

fun main() {
    println(a)
    if (a == b) println("==") else println("!=")
    val c = a.copy(name="c")
    println(c)
}

