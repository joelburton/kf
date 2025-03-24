package kf.words.custom

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.TextColors.gray
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.warning
import kf.*


object wToolsCustom : IWordModule {
    override val name = "kf.words.custom.wToolsCustom"
    override val description = "Tools specific to KF"
    override val words
        get() = arrayOf(
            Word(".DSTK", ::w_dotDstk),
            Word(".RSTK", ::w_dotRstk),
            Word(".CODE", ::w_dotCode),
            Word(".DATA", ::w_dotData),
            Word(".REGS", ::w_dotRegs),
            Word(".XT-SEE", ::w_dotXTSee),
            Word(".XT-SIMPLE-SEE", ::w_dotXTSeeSimple),
            Word(".SIMPLE-SEE", ::w_dotSeeSimple),
            Word(".IP@", ::w_dotIPFetch),
            Word(".IP!", ::w_dotIPStore),
            Word(".MEMCONFIG", ::w_dotMemConfig),
            Word(".DICT", ::w_dotDict),
            Word(".STACK-TRACE", ::w_dotStackTrace),
            Word(".SIMILAR", ::w_dotSimilar),
            Word("./WORDS", ::w_dotSlashWords),


            // ~~  *terminal*:lineno:char:<2> 20 10

        )

    fun _see(vm: ForthVM, w: Word, simple: Boolean) {
        val semiS = vm.dict[";s"]
        vm.io.print(w.getHeaderStr())
        w.deferToWn?.let {
            val src: Word = vm.dict[it]
            vm.io.println(" (deferrable word pointing to $src (${src.wn}))")
        }
        if (w.cpos == Word.Companion.NO_ADDR) {
            vm.io.println(" (built-in, cannot show code: ${w.getFnName()})")
        } else if (w.dpos != Word.Companion.NO_ADDR) {
            _dump(vm, w.dpos, simple)
        } else {
//                val ret_n: Int = vm.dict.getNum("return")
            for (k in w.cpos..<vm.cend) {
                _dump(vm, k, simple)
                if (vm.mem[k] == semiS.wn
                    && vm.cellMeta[k] == CellMeta.WordNum
                ) break
            }
        }
    }

    private fun _dump(vm: ForthVM, k: Int, simple: Boolean) {
        val v: Int = vm.mem[k]
        val exp = vm.cellMeta[k].getExplanation(vm, v, k)
            .apply { padEnd(20 - length) }
        val name = vm.dict.getByMem(k)?.let { "[word: ${it.name}]" } ?: ""
        vm.io.println(
            if (simple) "${k.addr} = $exp $name"
            else "${k.addr} = ${v.hex8} (${v.pad10}) $exp $name"
        )
    }

    /**  ( -- ) Dump the data stack. */
    fun w_dotDstk(vm: ForthVM) {
        vm.dstk.dump()
    }

    /**  ( -- ) Dump the return stack. */
    fun w_dotRstk(vm: ForthVM) {
        vm.rstk.dump()
    }


    /** Dump code area; this powers the ".text" word. */
    fun w_dotCode(vm: ForthVM) {
        for (k in vm.cstart..<vm.cend) {
            _dump(vm, k, false)
        }
    }

    fun w_dotRegs(vm: ForthVM) {
        for (k in vm.memConfig.regsStart..vm.memConfig.regsEnd) {
            _dump(vm, k, false)
        }
    }

    /** Dumps data area; this powers the ".data" word. */
    fun w_dotData(vm: ForthVM) {
        for (k in vm.dstart..<vm.dend) {
            _dump(vm, k, false)
        }
    }

    fun w_dotXTSee(vm: ForthVM) {
        val w: Word = vm.dict[vm.dstk.pop()]
        _see(vm, w, false)
    }


    fun w_dotXTSeeSimple(vm: ForthVM) {
        val w: Word = vm.dict[vm.dstk.pop()]
        _see(vm, w, true)
    }

    fun w_dotSeeSimple(vm: ForthVM) {
        val w = vm.dict[vm.source.scanner.parseName().strFromAddrLen(vm)]
        _see(vm, w, true)
    }

    fun w_dotIPFetch(vm: ForthVM) {
        vm.dstk.push(vm.ip)
    }

    fun w_dotIPStore(vm: ForthVM) {
        vm.ip = vm.dstk.pop()
    }

    fun w_dotMemConfig(vm: ForthVM) {
        vm.memConfig.show()
    }

    fun w_dotStackTrace(vm: ForthVM) {
        val stackTraceElements = Thread.currentThread().stackTrace
        vm.io.warning("Stack trace:")
        stackTraceElements.forEach {
            vm.io.println("    $it")
        }
    }

    /**  `.dict` ( -- : list all words with internal info )
     */
    fun w_dotDict(vm: ForthVM) {
        for (i in 0..<vm.dict.size) {
            val w: Word = vm.dict[i]
            vm.io.print(w.getHeaderStr())
        }
        vm.io.println(gray(Word.Companion.HEADER_STR))
    }

    /** `.SIMILAR` ( -- ) Find similar words */

    fun w_dotSimilar(vm: ForthVM) {
        val term = vm.source.scanner.parseName().strFromAddrLen(vm).lowercase()

        vm.io.println(
            vm.dict.words
                .filter { it.name.contains(term) }
                .joinToString(" ") { it.name },
            whitespace = Whitespace.NORMAL,
            overflowWrap = OverflowWrap.BREAK_WORD
        )
    }

    /** `./WORDS` ( -- n ) Get number of words */

    fun w_dotSlashWords(vm: ForthVM) {
        vm.dstk.push(vm.dict.size)
    }
}