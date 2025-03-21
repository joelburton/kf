package kf

import kotlin.math.absoluteValue


class StackOverflowError(name: String) :
    ForthError("${name}: Stack overflow")

class StackUnderflowError(name: String) :
    ForthError("${name}: Stack underflow")

class StackPtrInvalidError(name: String, n: Int) :
    ForthError("${name}: Stack ptr invalid: ${n}")

/** Stack in a memory location. This stack "grows downward" (ie, if sp=100,
 *   and then you push an item, sp will be 99).
 */

class FStack(
    val vm: ForthVM,
    val name: String,
    val startAt: Int,
    val endAt: Int,
) {
    var sp: Int = endAt + 1
    val size: Int
        get() = endAt - sp + 1

    /**  Get stack as array (for debugging). */
    fun asArray() = vm.mem.copyOfRange(sp, endAt + 1).reversedArray()

    /**  Just useful for debuggers. */
    override fun toString(): String = "$name ${asArray().contentToString()}"

    /** Retrieves value from stack at a index relative to the stack origin. */
    fun getAt(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackPtrInvalidError(name, n)
        return vm.mem[endAt - n]
    }

    /** Retrieves value from stack at a index relative to the stack pointer. */
    fun getFrom(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackPtrInvalidError(name, n)
        return vm.mem[sp + n]
    }

    /** Add item. */
    fun push(n: Int) {
        if (sp <= startAt) throw StackOverflowError(name)
        vm.mem[--sp] = n
    }

    /** Add two items (there's a variadic version below, but for performance
     *  with two items, we don't force a variadic call.
     */
    fun push(a: Int, b: Int) {
        if (sp - 1 <= startAt) throw StackOverflowError(name)
        vm.mem[--sp] = a
        vm.mem[--sp] = b
    }

    /** Add any number of items. */
    fun push(vararg vs: Int) {
        if (sp - vs.size <= startAt) throw StackOverflowError(name)
        for (v in vs) vm.mem[--sp] = v
    }

    /** Push double num in two parts (first lo, then high) */
    fun dblPush(n: Long) {
        if (n > ForthVM.MAX_INT) throw NumOutOfRange(n)
        push(n.toInt(), 0)

    // more to figure out, but holding onto this as a start:

        //        if (n >= 0) {
        //            val hi = (n ushr 32).toInt()
        //            val lo = (n and 0xFFFF_FFFF).toInt()
        //            push(lo, hi)
        //            println("POS $lo,$hi")
        //        } else {
        //            val na = n.absoluteValue
        //            val hi = -(na ushr 32).toInt() - 1
        //            val lo = -(na and 0xFFFF_FFFF).toInt()
        //            push(lo, hi)
        //            println("NEG $lo,$hi")
        //        }


    }

    /** Pop double num in two parts (high first, then lo) */
    fun dblPop(): Long {
        val hi = pop()
        if (hi != 0 && hi != -1) throw NumOutOfRange(hi.toLong())

        val lo = pop()
        return lo.toLong()

        // see above:
        //
        //        val hi = pop()
        //        val lo = pop()
        //        if (hi >= 0) {
        //            assert(lo >= 0)
        //            return (hi.toLong() shl 32) or (lo.toLong() and 0xFFFF_FFFF)
        //        } else {
        //            return (
        //                    (hi.toLong().inv() shl 32)
        //                            or (lo.toLong().inv() and 0xFFFF_FFFF)
        //                        .inv())
        //        }
    }

    /** Remove & return top item. */
    fun pop(): Int {
        if (sp > endAt) throw StackUnderflowError(name)
        return vm.mem[sp++]
    }

    /** Peek at top item. */
    fun peek(): Int = vm.mem[sp]

    /** Reset the stack. */
    fun reset() {
        sp = endAt + 1
    }

    /** Dump a single line for the stack; this is used by w_dotS */
    fun simpleDump() {
        val str = (endAt downTo sp).joinToString(separator = " ") {
            vm.mem[it].toString()
        }
        vm.io.print("<${size}> $str")
    }

    /**  Print a verbose stack dump. */
    fun dump() {
        if (D) vm.dbg(4, "$name: dump")
        for (i in 0..<size) {
            val v = getAt(i)
            vm.io.print("$name[$i] = ${v.hex8} (${v.pad10})")
            if (v in (0x20..0x7e)) {
                vm.io.print(" ${v.charRepr}")
            }
            if (i == size - 1)
                vm.io.print("   <- top")
            vm.io.println()
        }
    }

    fun popFrom(n: Int): Int {
        if (D) vm.dbg(3, "$name: popFrom $n")
        if (n < 0 || n >= size) throw StackPtrInvalidError(name, n)
        val v = vm.mem[sp + n]
        for (i in n downTo 1) {
            vm.mem[sp + i] = vm.mem[sp + i - 1]
        }
        sp += 1
        return v
    }


}