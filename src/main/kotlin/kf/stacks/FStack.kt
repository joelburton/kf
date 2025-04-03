package kf.stacks

import kf.*
import kf.interfaces.IFStack

class StackOverflowError(name: String) :
    StackError("${name}: Stack overflow")

class StackUnderflowError(name: String) :
    StackError("${name}: Stack underflow")

class StackPtrInvalidError(name: String, n: Int) :
    StackError("${name}: Stack ptr invalid: ${n}")


class FStack(
    val vm: ForthVM,
    override val name: String,
    override val startAt: Int,
    override val endAt: Int,
) : IFStack {
    override var sp: Int = endAt + 1
    override val size: Int get() = endAt - sp + 1

    override fun asArray() = vm.mem.copyOfRange(sp, endAt + 1).reversedArray()

    /**  Just useful for debuggers. */
    override fun toString(): String = "$name ${asArray().contentToString()}"

    override fun getAt(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackPtrInvalidError(name, n)
        return vm.mem[endAt - n]
    }

    override fun getFrom(n: Int): Int {
        if (n < 0 || n > endAt - sp) throw StackPtrInvalidError(name, n)
        return vm.mem[sp + n]
    }

    override fun popFrom(n: Int): Int {
        if (D) vm.dbg(3, "$name: popFrom $n")

        if (n < 0 || n >= size) throw StackPtrInvalidError(name, n)
        val v = vm.mem[sp + n]
        for (i in n downTo 1) {
            vm.mem[sp + i] = vm.mem[sp + i - 1]
        }
        sp += 1
        return v
    }

    override fun push(n: Int) {
        if (sp <= startAt) throw StackOverflowError(name)
        vm.mem[--sp] = n
    }


    override fun push(a: Int, b: Int) {
        if (sp - 1 <= startAt) throw StackOverflowError(name)
        vm.mem[--sp] = a
        vm.mem[--sp] = b
    }

    override fun push(vararg vs: Int) {
        if (sp - vs.size <= startAt) throw StackOverflowError(name)
        for (v in vs) vm.mem[--sp] = v
    }

    override fun dblPush(n: Long) {
        if (n > ForthVM.Companion.MAX_INT) throw NumOutOfRange(n)
        push(n.toInt(), if (n < 0) -1 else 0)
    }

    override fun dblPop(): Long {
        val hi = pop()
        if (hi != 0 && hi != -1) throw NumOutOfRange(hi.toLong())

        val lo = pop()
        return lo.toLong()
    }

    override fun pop(): Int {
        if (sp > endAt) throw StackUnderflowError(name)
        return vm.mem[sp++]
    }

    override fun peek(): Int = vm.mem[sp]

    /** Reset the stack. */
    internal fun reset() {
        sp = endAt + 1
    }

    override fun simpleDumpStr(): String {
        val str = (endAt downTo sp).joinToString("") {
            "${vm.mem[it].numToStrPrefixed(vm.base)} "
        }
        return "<${size}> $str"
    }

    override fun simpleDump() = vm.io.print(simpleDumpStr())

    override fun dump() {
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