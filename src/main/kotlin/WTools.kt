package kf

class WTools(val vm: ForthVM) : WordClass {
    override val name = "Tools"
    override val primitives: Array<Word> = arrayOf(
        Word(".dstk") { w_dumpDataStack() },
        Word(".rstk") { w_dumpReturnStack() },
        Word(".cptr") { w_cptr() },
        Word(".code") { w_dumpCode() },
        Word(".data") { w_dumpData() },
        Word(".regs") { w_dumpRegs() },
        Word("_see") { w_xt_see() },
        Word("see") { w_see() },
        Word("_simple-see") { w_xt_seeSimple() },
        Word("simple-see") { w_seeSimple() },

        // ~~  *terminal*:lineno:char:<2> 20 10

    )

    companion object {
        fun _see(vm: ForthVM, w: Word, simple: Boolean) {
            vm.io.output.print(w.getHeaderStr(vm.io))

            if (w.cpos == Word.NO_ADDR) {
                vm.io.output.println(" (built-in, cannot show code)")
            } else if (w.dpos != Word.NO_ADDR) {
//            int data = vm.mem[w.dpos];
                _dump(vm, w.dpos, simple)
            } else {
                val ret_n: Int = vm.dict.getNum("return")
                for (k in w.cpos..<vm.cend) {
                    _dump(vm, k, simple)
                    if (vm.mem.get(k) == ret_n) break
                }
            }
        }

        private fun _dump(vm: ForthVM, k: Int, simple: Boolean) {
            val v: Int = vm.mem.get(k)
            val w: Word? = vm.dict.getByMem(k)
            val meta: CellMeta = vm.cellMeta.get(k)
            val explanation: String = meta.getExplanation(vm, v)

            if (!simple) {
                vm.io.output.printf(
                    "$%04x = $%08x (%10d) %-20s%s",
                    k,
                    v,
                    v,
                    explanation,
                    if (w != null) "[word: " + w.name + "]" else ""
                )
            } else {
                vm.io.output.printf(
                    "0x%04x = %-20s%s",
                    k,
                    explanation,
                    if (w != null) "[word: " + w.name + "]" else ""
                )
            }
            vm.io.output.println()
        }
    }

    /**  ( -- ) Dump the data stack. */
    fun w_dumpDataStack() {
        vm.dstk.dump()
    }

    /**  ( -- ) Dump the return stack. */
    fun w_dumpReturnStack() {
        vm.rstk.dump()
    }


    fun w_cptr() {
        vm.dstk.push(vm.ip)
    }


    /** Dump code area; this powers the ".text" word. */
    fun w_dumpCode() {
        for (k in vm.cstart..<vm.cend) {
            _dump(vm, k, false)
        }
    }

    fun w_dumpRegs() {
        for (k in vm.memConfig.regsStart..vm.memConfig.regsEnd) {
            _dump(vm, k, false)
        }
    }


    /** Dumps data area; this powers the ".data" word. */
    fun w_dumpData() {
        for (k in vm.dstart..<vm.dend) {
            _dump(vm, k, false)
        }
    }

    fun w_xt_see() {
        val wn: Int = vm.dstk.pop()
        val w: Word = vm.dict.get(wn)
        _see(vm, w, false)
    }

    fun w_see() {
        val w: Word = vm.dict.get(vm.getToken())
        _see(vm, w, false)
    }


    fun w_xt_seeSimple() {
        val w: Word = vm.dict.get(vm.dstk.pop())
        _see(vm, w, true)
    }

    fun w_seeSimple() {
        val name: String = vm.getToken()
        val w: Word = vm.dict.get(name)
        _see(vm, w, true)
    }
}