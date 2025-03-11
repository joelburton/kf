package kf


class WMemory(val vm: ForthVM) : WordClass {
    override val name = "Memory"
    override val primitives: Array<Word> = arrayOf<Word>(
        Word("@") { _ -> w_fetch() },
        Word("!") { _ -> w_store() },
        Word("here") { _ -> w_here() },
        Word("allot") { _ -> w_allot() },
        Word(",") { _ -> w_comma() },
        Word(",,") { _ -> w_commaComma() },
        Word("?") { _ -> w_question() },
        Word(".cstart") { _ -> w_cstart() },
        Word(".cend") { _ -> w_cend() },
        Word(".dstart") { _ -> w_dstart() },
        Word(".dend") { _ -> w_dend() },
        Word("dump") { _ -> w_dump() },
        Word("!+") { _ -> w_plusBang() },
        Word("cell") { _ -> w_cell() },
        Word("cells") { _ -> w_cells() },
        Word("unused") { _ -> w_unused() },
        Word("on") { _ -> w_on() },
        Word("off") { _ -> w_off() },
        Word("erase") { _ -> w_erase() },
        Word("fill") { _ -> w_fill() },
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
        vm.dbg("w_unused")
        vm.dstk.push(vm.memConfig.dataEnd - vm.dend + 1)
    }

    // addr on  = set to true
    private fun w_on() {
        vm.mem[vm.dstk.pop()] = WMathLogic.TRUE
    }

    // addr off = set to false
    private fun w_off() {
        vm.mem[vm.dstk.pop()] = WMathLogic.FALSE
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
        if (D) vm.dbg("w_fetch: addr=%04x num=%d", addr, num)
        vm.dstk.push(num)
    }

    /**  ( n addr -- ) ! Store data at addr. */
    fun w_store() {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.dstk.pop()
        if (D) vm.dbg("w_store: addr=%04x num=%d", addr, num)
        vm.mem[addr] = num
    }

    /**  ( -- n ) Push value of here (section of DATA where will be written) */
    fun w_here() {
        if (D) vm.dbg("w_here")
        vm.dstk.push(vm.dend)
    }

    /**  ( n -- ) Get n spaces in data section. */
    fun w_allot() {
        val d = vm.dstk.pop()
        vm.dbg("w_allot: $d")
        vm.dend += d
    }

    fun w_dstart() {
        vm.dstk.push(vm.memConfig.dataStart)
    }

    fun w_dend() {
        vm.dstk.push(ForthVM.REG_DEND)
    }

    fun w_cstart() {
        vm.dstk.push(vm.memConfig.codeStart)
    }

    fun w_cend() {
        vm.dstk.push(ForthVM.REG_CEND)
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
        if (D) vm.dbg("w_commaComma: cend=%d", vm.cend)
        vm.mem[vm.cend++] = vm.dstk.pop()
    }

    fun w_question() {
        val `val`: Int = vm.mem.get(vm.dstk.pop())
        if (D) vm.dbg("w_question: val=%d", `val`)
        vm.io.output.print(`val`.toString(vm.base.coerceIn(2, 36)) + " ")
    }
}