package kf.stacks

import kf.*

class StackOverflowError(name: String) :
    StackError("${name}: Stack overflow")

class StackUnderflowError(name: String) :
    StackError("${name}: Stack underflow")

class StackPtrInvalidError(name: String, n: Int) :
    StackError("${name}: Stack ptr invalid: ${n}")


/** Stack in a memory location.
 *
 * This stack "grows downward" (ie, if sp=100, and then you push an item, sp
 * will be 99).
 */

class FStack(
    val vm: ForthVM,
    val name: String,
    /** Location in memory of start of stack. */
    val startAt: Int,
    /** Location in memory of end of stack-space */
    val endAt: Int,
) {
    /** Pointer to the current item. */
    var sp: Int = endAt + 1
    /** Number of items in the stack. */
    val size: Int get() = endAt - sp + 1

    /**  Get stack as array (for debugging). */
    fun asArray() = vm.mem.copyOfRange(sp, endAt + 1).reversedArray()

    /**  Just useful for debuggers. */
    override fun toString(): String = "$name ${asArray().contentToString()}"

    /** Retrieves value from stack at a index relative to the stack origin.
     *
     * getAt(0) would be the first item ever added to the stack.
     * */
    internal fun getAt(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackPtrInvalidError(name, n)
        return vm.mem[endAt - n]
    }

    /** Retrieves value from stack at a index relative to the stack pointer.
     *
     * getFrom(0) is the same as peek()
     * */
    fun getFrom(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackPtrInvalidError(name, n)
        return vm.mem[sp + n]
    }

    /** Remove & return value at index relative to stack pointer.
     *
     * Used to implement `ROLL`
     * */

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


    /** Add item. */
    fun push(n: Int) {
        if (sp <= startAt) throw StackOverflowError(name)
        vm.mem[--sp] = n
    }

    /** Add two items.
     *
     * Just an optimization, given how common this is. This reduces the number
     * of separate function calls and will perform better than the truly
     * variadic version,below.
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

    /** Push double num in two parts (first lo, then high)
     *
     * The project doesn't truly support double nums (they're not so urgent
     * when the normal cell size is already 32 bits). But there are lots of
     * API compatibility needed here. This takes a long, but only ever adds
     * the lower 32 bits and a 0 for the hi-cell.
     * */
    fun dblPush(n: Long) {
        if (n > ForthVM.Companion.MAX_INT) throw NumOutOfRange(n)
        push(n.toInt(), if (n < 0) -1 else 0)
    }

    /** Pop double num in two parts (high first, then lo) */
    fun dblPop(): Long {
        val hi = pop()
        if (hi != 0 && hi != -1) throw NumOutOfRange(hi.toLong())

        val lo = pop()
        return lo.toLong()

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

    fun simpleDumpStr(): String {
        val str = (endAt downTo sp).joinToString("") {
            "${vm.mem[it].numToStrPrefixed(vm.base)} "
        }
        return "<${size}> $str"
    }

    /** Dump a single line for the stack; this is used by `.S` */
    fun simpleDump()  = vm.io.print(simpleDumpStr())

    /**  Print a verbose stack dump. */
    fun dump() {
        if (D) vm.dbg(4, "$name: dump")

        for (i in 0..<size) {
            val v = getAt(i)
            vm.io.print("$name[$i] = ${v.hex8} (${v.pad10}) ${v.charRepr}")
            if (i == size - 1)
                vm.io.print("   <- top")
            vm.io.println()
        }
    }
}