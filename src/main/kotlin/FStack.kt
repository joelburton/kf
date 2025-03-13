package kf

class StackOverflow(name: String) :
    ForthError("${name}: Stack overflow")

class StackUnderflow(name: String) :
    ForthError("${name}: Stack underflow")

class StackInvalid(name: String, n: Int) :
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
    override fun toString(): String = "${name} ${asArray().contentToString()}"

    /** Retrieves value from stack at a index relative to the stack origin. */
    fun getAt(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackInvalid(name, n)
        return vm.mem[endAt - n]
    }

    /** Retrieves value from stack at a index relative to the stack pointer. */
    fun getFrom(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackInvalid(name, n)
        return vm.mem[sp + n]
    }

    /** Add item. */
    fun push(n: Int) {
        if (sp <= startAt) throw StackOverflow(name)
        vm.mem[--sp] = n
    }

    /** Add two items (there's a variadic version below, but for performance
     *  with two items, we don't force a variadic call.
     */
    fun push(a: Int, b: Int) {
        if (sp - 1 <= startAt) throw StackOverflow(name)
        vm.mem[--sp] = a
        vm.mem[--sp] = b
    }

    /** Add any number of items. */
    fun push(vararg vs: Int) {
        if (sp - vs.size <= startAt) throw StackOverflow(name)
        for (v in vs) vm.mem[--sp] = v
    }

    /** Remove & return top item. */
    fun pop(): Int {
        if (sp > endAt) throw StackUnderflow(name)
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
        vm.io.print("<${size}> ${str}")
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
        if (n < 0 || n >= size) throw StackInvalid(name, n)
        val v = vm.mem[sp + n]
        for (i in n downTo 1) {
            vm.mem[sp + i] = vm.mem[sp + i - 1]
        }
        sp += 1
        return v
    }


}