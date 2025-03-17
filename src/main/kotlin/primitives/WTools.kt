package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass
import kf.addr
import kf.hex8
import kf.pad10

object WTools : WordClass {
    override val name = "Tools"
    override val primitives get() = arrayOf(
        Word(".dstk", ::w_dumpDataStack),
        Word(".rstk", ::w_dumpReturnStack),
        Word(".code", ::w_dumpCode),
        Word(".data", ::w_dumpData),
        Word(".regs", ::w_dumpRegs),
        Word("xt-see", ::w_xtSee),
        Word("see", ::w_see),
        Word("xt-simple-see", ::w_xtSeeSimple),
        Word("simple-see", ::w_seeSimple),

        // ~~  *terminal*:lineno:char:<2> 20 10

    )

    fun _see(vm: ForthVM, w: Word, simple: Boolean) {
        vm.io.print(w.getHeaderStr())
        w.deferToWn?.let {
            val src: Word = vm.dict[it]
            vm.io.println(" (deferrable word pointing to $src (${src.wn}))")
        }
        if (w.cpos == Word.Companion.NO_ADDR) {
            vm.io.println(" (built-in, cannot show code)")
        } else if (w.dpos != Word.Companion.NO_ADDR) {
            _dump(vm, w.dpos, simple)
        } else {
//                val ret_n: Int = vm.dict.getNum("return")
            for (k in w.cpos..<vm.cend) {
                _dump(vm, k, simple)
                if (w.cposEnd == k) break
            }
        }
    }

    private fun _dump(vm: ForthVM, k: Int, simple: Boolean) {
        val v: Int = vm.mem[k]
        val exp = vm.cellMeta[k].getExplanation(vm, v)
            .apply { padEnd(20 - length) }
        val name = vm.dict.getByMem(k)?.let { "[word: ${it.name}]" } ?: ""
        vm.io.println(
            if (simple) "${k.addr} = $exp $name"
            else "${k.addr} = ${v.hex8} (${v.pad10}) $exp $name"
        )
    }

    /**  ( -- ) Dump the data stack. */
    fun w_dumpDataStack(vm: ForthVM) {
        vm.dstk.dump()
    }

    /**  ( -- ) Dump the return stack. */
    fun w_dumpReturnStack(vm: ForthVM) {
        vm.rstk.dump()
    }


    /** Dump code area; this powers the ".text" word. */
    fun w_dumpCode(vm: ForthVM) {
        for (k in vm.cstart..<vm.cend) {
            _dump(vm, k, false)
        }
    }

    fun w_dumpRegs(vm: ForthVM) {
        for (k in vm.memConfig.regsStart..vm.memConfig.regsEnd) {
            _dump(vm, k, false)
        }
    }

    /** Dumps data area; this powers the ".data" word. */
    fun w_dumpData(vm: ForthVM) {
        for (k in vm.dstart..<vm.dend) {
            _dump(vm, k, false)
        }
    }

    fun w_xtSee(vm: ForthVM) {
        val w: Word = vm.dict[vm.dstk.pop()]
        _see(vm, w, false)
    }

    fun w_see(vm: ForthVM) {
        val w: Word = vm.dict[vm.getToken()]
        _see(vm, w, false)
    }

    fun w_xtSeeSimple(vm: ForthVM) {
        val w: Word = vm.dict[vm.dstk.pop()]
        _see(vm, w, true)
    }

    fun w_seeSimple(vm: ForthVM) {
        val w: Word = vm.dict[vm.getToken()]
        _see(vm, w, true)
    }
}