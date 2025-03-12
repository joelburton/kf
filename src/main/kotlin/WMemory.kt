package kf


class WMemory(val vm: ForthVM) : WordClass {
    override val name = "Memory"
    override val primitives: Array<Word> = arrayOf(
        Word("@") { w_fetch() },
        Word("!") { w_store() },
        Word("here") { w_here() },
        Word("allot") { w_allot() },
        Word(",") { w_comma() },
        Word(",,") { w_commaComma() },
        Word("?") { w_question() },
        Word("dump") { w_dump() },
        Word("!+") { w_plusBang() },
        Word("cell") { w_cell() },
        Word("cells") { w_cells() },
        Word("unused") { w_unused() },
        Word("on") { w_on() },
        Word("off") { w_off() },
        Word("erase") { w_erase() },
        Word("fill") { w_fill() },
    )

    private fun w_cell() {
        vm.dstk.push(1)
    }

    private fun w_cells() {
        val size: Int = vm.dstk.pop()
        vm.dstk.push(size)
    }

    private fun w_plusBang() {
        val addr: Int = vm.dstk.pop()
        vm.mem[addr]++
    }

    private fun w_unused() {
        vm.dstk.push(vm.memConfig.dataEnd - vm.dend + 1)
    }

    // addr on  = set to true
    private fun w_on() {
        vm.mem[vm.dstk.pop()] = ForthVM.TRUE
    }

    // addr off = set to false
    private fun w_off() {
        vm.mem[vm.dstk.pop()] = ForthVM.FALSE
    }

    //addr u erase
    private fun w_erase() {
        val len: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = 0
        }
    }

    // addr u c fill
    private fun w_fill() {
        val fillWith: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        val len: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = fillWith
        }
    }

    /**  ( addr -- n ) Get data from addr. */
    fun w_fetch() {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.mem.get(addr)
        vm.dstk.push(num)
    }

    /**  ( n addr -- ) ! Store data at addr. */
    fun w_store() {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.dstk.pop()
        vm.mem[addr] = num
    }

    /**  ( -- n ) Push value of here (section of DATA where will be written) */
    fun w_here() {
        vm.dstk.push(vm.dend)
    }

    /**  ( n -- ) Get n spaces in data section. */
    fun w_allot() {
        val d = vm.dstk.pop()
        vm.dend += d
    }

    fun w_dump() {
        val len: Int = vm.dstk.pop()
        val start: Int = vm.dstk.pop()
        val end = start + len - 1
        var i = start - (start % 4)
        while (i < start + len) {
            val a = if (i >= start && i <= end)
                String.format("%08x", vm.mem.get(i))
            else
                "        "
            val b = if (i + 1 >= start && i + 1 <= end)
                String.format("%08x", vm.mem.get(i + 1))
            else
                "        "
            val c = if (i + 2 >= start && i + 2 <= end)
                String.format("%08x", vm.mem.get(i + 2))
            else
                "        "
            val d = if (i + 3 >= start && i + 3 <= end)
                String.format("%08x", vm.mem.get(i + 3))
            else
                "        "
            vm.io.output.printf("0x%04x = %s %s %s %s\n", i, a, b, c, d)
            i += 4
        }
    }

    fun w_comma() {
        vm.mem[vm.dend++] = vm.dstk.pop()
    }

    fun w_commaComma() {
        vm.mem[vm.cend++] = vm.dstk.pop()
    }

    fun w_question() {
        val v: Int = vm.mem.get(vm.dstk.pop())
        vm.io.output.print(v.toString(vm.base.coerceIn(2, 36)) + " ")
    }
}