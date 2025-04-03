package kf.words.core

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord

object wMemory : IWordModule {
    override val name = "kf.words.core.wMemory"
    override val description = "High-level memory"

    override val words
        get() = arrayOf<IWord>(
            Word("@", ::w_fetch),
            Word("!", ::w_store),
            Word("+!", ::w_plusStore),
            Word("2@", ::w_twoFetch),
            Word("2!", ::w_twoStore),

            Word(",", ::w_comma),
            Word("HERE", ::w_here),

            Word("FILL", ::w_fill),
            Word("MOVE", ::w_move),
            Word("ALLOT", ::w_allot),

            Word("CELL+", ::w_cellPlus),
            Word("CELLS", ::w_cells),
            Word("ALIGN", ::w_align),
            Word("ALIGNED", ::w_aligned),
        )

    /** `@` ( a-addr -- x ) x is the value stored at a-addr */

    fun w_fetch(vm: IForthVM) {
        val addr = vm.dstk.pop()
        val num = vm.mem[addr]
        vm.dstk.push(num)
    }

    /** `!` ( x a-addr -- ) Store x at a-addr */

    fun w_store(vm: IForthVM) {
        val addr: Int = vm.dstk.pop()
        val num: Int = vm.dstk.pop()
        vm.mem[addr] = num
    }

    /** `+!` ( n a-addr -- ) Add n to the single-cell number at a-addr */

    fun w_plusStore(vm: IForthVM) {
        val addr: Int = vm.dstk.pop()
        val incr = vm.dstk.pop()
        vm.mem[addr] = vm.mem[addr] + incr
    }

    /** `2@` ( a-addr -- x1 x2 ) Fetch pair x1 (addr) x2 (addr+1)  */

    fun w_twoFetch(vm: IForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(vm.mem[addr], vm.mem[addr + 1])
    }

    /** `2!` ( x1 x2 a-addr -- ) Store pair x1 (to addr+1) x2 (to addr) */

    fun w_twoStore(vm: IForthVM) {
        val addr = vm.dstk.pop()
        val x2 = vm.dstk.pop()
        val x1 = vm.dstk.pop()
        vm.mem[addr] = x2
        vm.mem[addr + 1] = x1
    }


    /** `,` ( x -- ) Write x to data-end */

    fun w_comma(vm: IForthVM) {
        vm.mem[vm.dend++] = vm.dstk.pop()
    }

    /** `HERE` ( -- addr ) Addr is dend address */

    fun w_here(vm: IForthVM) {
        vm.dstk.push(vm.dend)
    }



    /** `FILL` ( c-addr u char -- ) Store char in u cells starting a c-addr */

    fun w_fill(vm: IForthVM) {
        val char = vm.dstk.pop()
        val count = vm.dstk.pop()
        val addr = vm.dstk.pop()
        for (i in 0 until count) {
            vm.mem[addr + i] = char
        }
    }

    /** `MOVE` ( addr1 addr2 u -- ) Copy u cells from addr1 to addr2
     *
     * Needs to handle overlapping memory ranges.
     * */

    fun w_move(vm: IForthVM) {
        val count = vm.dstk.pop()
        val addr2 = vm.dstk.pop()
        val addr1 = vm.dstk.pop()
        for (i in 0 until count) {
            vm.mem[addr2 + i] = vm.mem[addr1 + i]
        }
    }

    /** `ALLOT` ( n -- ) Get n spaces in data section. */

    fun w_allot(vm: IForthVM) {
        val d = vm.dstk.pop()
        vm.dend += d
    }


    /** `CELL+` ( a-addr1 -- a-addr2 ) Add size of addr to addr1 => addr2 */

    fun w_cellPlus(vm: IForthVM) {
        val addr = vm.dstk.pop()
        vm.dstk.push(addr + 1)
    }

    /** `CELLS` ( n1 -- n2 ) n2 is size of n1 cells */

    fun w_cells(vm: IForthVM) {
        val num = vm.dstk.pop()
        vm.dstk.push(num)
    }

    /** `ALIGN` ( -- ) If the dend is not aligned, reserve space to align
     *
     * Given that our cells (char or regular) are size-1, they always are.
     * */

    fun w_align(vm: IForthVM) {
        /* always aligned */
    }

    /** `ALIGNED` ( addr -- a-addr ) a-addr is aligned address >= addr
     *
     * Given that our cells (char or regular) are size-1, these are same.
     */

    fun w_aligned(vm: IForthVM) {
        /* always aligned */
    }
}