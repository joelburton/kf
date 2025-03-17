package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass
import kf.addr
import kf.hex8


object WMemory : WordClass {
    override val name = "Memory"
    override val primitives get() = arrayOf(
        Word("@", ::w_fetch ) ,
        Word("!", ::w_store ) ,
        Word("+!", ::w_plusStore),
        Word("here", ::w_here ) ,
        Word("allot", ::w_allot ) ,
        Word(",", ::w_comma ) ,
        Word(",,", ::w_commaComma ) ,
        Word("?", ::w_question ) ,
        Word("dump", ::w_dump ) ,
        Word("!+", ::w_plusStore ) ,
        Word("cell", ::w_cell ) ,
        Word("cells", ::w_cells ) ,
        Word("unused", ::w_unused ) ,
        Word("on", ::w_on ) ,
        Word("off", ::w_off ) ,
        Word("erase", ::w_erase ) ,
        Word("fill", ::w_fill ) ,
        Word("cells+", ::w_cellsPlus),
    )

    private fun w_cell(vm: ForthVM) {
        vm.dstk.push(1)
    }

    private fun w_cells(vm: ForthVM) {
        val size: Int = vm.dstk.pop()
        vm.dstk.push(size)
    }

//    private fun w_plusBang(vm: ForthVM) {
//        val addr: Int = vm.dstk.pop()
//        vm.mem[addr]++
//    }

    private fun w_unused(vm: ForthVM) {
        vm.dstk.push(vm.memConfig.dataEnd - vm.dend + 1)
    }

    // addr on  = set to true
    private fun w_on(vm: ForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.TRUE
    }

    // addr off = set to false
    private fun w_off(vm: ForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.FALSE
    }

    //addr u erase
    private fun w_erase(vm: ForthVM) {
        val len: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = 0
        }
    }

    // addr u c fill
    private fun w_fill(vm: ForthVM) {
        val fillWith: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        val len: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = fillWith
        }
    }

    /**  ( addr -- n ) Get data from addr. */
    fun w_fetch(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.mem[addr]
        vm.dstk.push(num)
    }

    /**  ( n addr -- ) ! Store data at addr. */
    fun w_store(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.dstk.pop()
        vm.mem[addr] = num
    }

    fun w_plusStore(vm: ForthVM) {
        val addr : Int = vm.dstk.pop()
        val incr = vm.dstk.pop()
        vm.mem[addr] = vm.mem[addr] + incr

    }

    /**  ( -- n ) Push value of here (section of DATA where will be written) */
    fun w_here(vm: ForthVM) {
        vm.dstk.push(vm.dend)
    }

    /**  ( n -- ) Get n spaces in data section. */
    fun w_allot(vm: ForthVM) {
        val d = vm.dstk.pop()
        vm.dend += d
    }

    fun w_dump(vm: ForthVM) {
        val len: Int = vm.dstk.pop()
        val start: Int = vm.dstk.pop()
        val end = start + len - 1
        var i = start - (start % 4)
        while (i < start + len) {
            val a = if (i >= start && i <= end) vm.mem[i].hex8
            else "        "

            val b = if (i + 1 >= start && i + 1 <= end) vm.mem[i+1].hex8
            else "        "

            val c = if (i + 2 >= start && i + 2 <= end) vm.mem[i+2].hex8
            else "        "

            val d = if (i + 3 >= start && i + 3 <= end) vm.mem[i+3].hex8
            else "        "

            vm.io.println("${i.addr} = $a $b $c $d")
            i += 4
        }
    }

    fun w_comma(vm: ForthVM) {
        vm.mem[vm.dend++] = vm.dstk.pop()
    }

    fun w_commaComma(vm: ForthVM) {
        vm.mem[vm.cend++] = vm.dstk.pop()
    }

    fun w_question(vm: ForthVM) {
        val v: Int = vm.mem[vm.dstk.pop()]
        vm.io.print(v.toString(vm.base.coerceIn(2, 36)) + " ")
    }

    fun w_cellsPlus(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val num: Int = vm.dstk.pop()
        vm.dstk.push(addr + num)
    }
}