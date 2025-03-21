package kf.words.core

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wMemory : IWordClass {
    override val name = "Memory"
    override val description = "High-level memory"

    override val words
        get() = arrayOf(
            Word("!", ::w_store),
            Word("+!", ::w_plusStore),
            Word("2@", ::w_twoFetch),
            Word("2!", ::w_twoStore),
            Word("@", ::w_fetch),
            Word("FILL", ::w_fill),
            Word("MOVE", ::w_move),
            Word("CELL+", ::w_cellPlus),
            Word("CELLS", ::w_cells),
            Word(",", ::w_comma),
            Word("HERE", ::w_here),
            Word("ALIGN", ::w_align),
            Word("ALIGNED", ::w_aligned),
            Word("ALLOT", ::w_allot),
        )

    /** !    store   CORE
     *
     * ( x a-addr -- )
     *
     * Store x at a-addr.
     */

    fun w_store(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.dstk.pop()
        vm.mem[addr] = num
    }

    /** +!   plus-store  CORE
     *
     * ( n | u a-addr -- )
     *
     * Add n | u to the single-cell number at a-addr.
     */

    fun w_plusStore(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        val incr = vm.dstk.pop()
        vm.mem[addr] = vm.mem[addr] + incr
    }

    /** 2@   two-fetch   CORE
     *
     * ( a-addr -- x1 x2 )
     *
     * Fetch the cell pair x1 x2 stored at a-addr. x2 is stored at a-addr and
     * x1 at the next consecutive cell. It is equivalent to the sequence DUP
     * CELL+ @ SWAP @.
     */

    fun w_twoFetch(vm: ForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(vm.mem[addr])
        vm.dstk.push(vm.mem[addr + 1])
    }

    /** 2!   two-store   CORE
     *
     * ( x1 x2 a-addr -- )
     *
     * Store the cell pair x1 x2 at a-addr, with x2 at a-addr and x1 at the
     * next consecutive cell. It is equivalent to the sequence SWAP OVER !
     * CELL+ !.
     */

    fun w_twoStore(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.mem[addr] = x1
        vm.mem[addr + 1] = x2
    }

    /** @    fetch   CORE
     *
     * ( a-addr -- x )
     *
     * x is the value stored at a-addr.
     */

    fun w_fetch(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val num = vm.mem[addr]
        vm.dstk.push(num)
    }

    /** FILL     CORE
     *
     * ( c-addr u char -- )
     *
     * If u is greater than zero, store char in each of u consecutive
     * characters of memory beginning at c-addr.
     */

    fun w_fill(vm: ForthVM) {
        val char = vm.dstk.pop()
        val count = vm.dstk.pop()
        val addr = vm.dstk.pop()
        for (i in 0 until count) {
            vm.mem[addr + i] = char
        }
    }

    /** MOVE     CORE
     *
     * ( addr1 addr2 u -- )
     *
     * If u is greater than zero, copy the contents of u consecutive address
     * units at addr1 to the u consecutive address units at addr2. After MOVE
     * completes, the u consecutive address units at addr2 contain exactly what
     * the u consecutive address units at addr1 contained before the move.
     */

    fun w_move(vm: ForthVM) {
        val count = vm.dstk.pop()
        val addr2 = vm.dstk.pop()
        val addr1 = vm.dstk.pop()
        for (i in 0 until count) {
            vm.mem[addr2 + i] = vm.mem[addr1 + i]
        }
    }

    /** CELL+    cell-plus   CORE
     *
     * ( a-addr1 -- a-addr2 )
     *
     * Add the size in address units of a cell to a-addr1, giving a-addr2.
     */

    fun w_cellPlus(vm: ForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(addr + 1)
    }

    /** CELLS    CORE
     *
     * ( n1 -- n2 )
     *
     * n2 is the size in address units of n1 cells.
     */

    fun w_cells(vm: ForthVM) {
        val num = vm.dstk.pop()
        vm.dstk.push(num)
    }

    /** ,    comma   CORE
     *
     * ( x -- )
     *
     * Reserve one cell of data space and store x in the cell. If the
     * data-space pointer is aligned when , begins execution, it will remain
     * aligned when , finishes execution. An ambiguous condition exists if the
     * data-space pointer is not aligned prior to execution of ,.
     */

    fun w_comma(vm: ForthVM) {
        vm.mem[vm.dend++] = vm.dstk.pop()
    }

    /** HERE     CORE
     *
     * ( -- addr )
     *
     * addr is the data-space pointer.
     */

    fun w_here(vm: ForthVM) {
        vm.dstk.push(vm.dend)
    }

    /** ALIGN    CORE
     *
     * ( -- )
     *
     * If the data-space pointer is not aligned, reserve enough space to align
     * it.
     */

    fun w_align(vm: ForthVM) {
        /* always aligned */
    }

    /** ALIGNED  CORE
     *
     * ( addr -- a-addr )
     *
     * a-addr is the first aligned address greater than or equal to addr.
     */

    fun w_aligned(vm: ForthVM) {
        /* always aligned */
    }

    /**  ( n -- ) Get n spaces in data section. */
    fun w_allot(vm: ForthVM) {
        val d = vm.dstk.pop()
        vm.dend += d
    }

}