package kf.words.custom

import kf.*
import kf.dict.Dict
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord
import kf.mem.CellMeta
import kf.stacks.FStack
import org.jline.utils.ExecHelper.exec
import java.io.IOException


object wToolsCustom : IWordModule {
    override val name = "kf.words.custom.wToolsCustom"
    override val description = "Tools specific to KF"
    override val words
        get() = arrayOf<IWord>(
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
            Word(".CS", ::w_dotCS),
            Word(".HISTORY", ::w_dotHistory),
            Word(".LESS", ::w_dotLess),
            Word(".SHELL", ::w_dotShell),
            Word("~~", ::w_tildeTilde),
            Word(".RERUN", ::w_dotReRun),
            Word(".TERM-INFO", ::w_dotTermInfo),

            // ~~  *terminal*:lineno:char:<2> 20 10

        )

    fun _see(vm: ForthVM, w: Word, simple: Boolean) {
        val semiS = vm.dict[";s"]
        vm.io.info(vm.dict.getHeaderStr(w))
        w.deferToWn?.let {
            val src = vm.dict[it]
            vm.io.println(" (deferrable word pointing to $src (${src.wn}))")
        }
        if (w.cpos == 0xffff) {
            vm.io.println(" (built-in, cannot show code: ${vm.dict.getFnName(w)})")
        } else if (w.dpos != 0xffff) {
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

    private fun _dump(inVm: IForthVM, k: Int, simple: Boolean) {
        val vm = inVm as ForthVM
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
    fun w_dotDstk(vm: IForthVM) {
        vm.dstk.dump()
    }

    /**  ( -- ) Dump the return stack. */
    fun w_dotRstk(vm: IForthVM) {
        vm.rstk.dump()
    }


    /** Dump code area; this powers the ".text" word. */
    fun w_dotCode(vm: IForthVM) {
        for (k in vm.cstart..<vm.cend) {
            _dump(vm, k, false)
        }
    }

    fun w_dotRegs(vm: IForthVM) {
        for (k in vm.memConfig.regsStart..vm.memConfig.regsEnd) {
            _dump(vm, k, false)
        }
    }

    /** Dumps data area; this powers the ".data" word. */
    fun w_dotData(vm: IForthVM) {
        for (k in vm.dstart..<vm.dend) {
            _dump(vm, k, false)
        }
    }

    fun w_dotXTSee(vm: IForthVM) {
        val w = vm.dict[vm.dstk.pop()]
        _see(vm as ForthVM, w as Word, false)
    }


    fun w_dotXTSeeSimple(vm: IForthVM) {
        val w = vm.dict[vm.dstk.pop()]
        _see(vm as ForthVM, w as Word, true)
    }

    fun w_dotSeeSimple(vm: IForthVM) {
        val w = vm.dict[vm.source.scanner.parseName().strFromAddrLen(vm)]
        _see(vm as ForthVM, w as Word, true)
    }

    fun w_dotIPFetch(vm: IForthVM) {
        vm.dstk.push(vm.ip)
    }

    fun w_dotIPStore(vm: IForthVM) {
        vm.ip = vm.dstk.pop()
    }

    fun w_dotMemConfig(vm: IForthVM) {
        vm.memConfig.show()
    }

    fun w_dotStackTrace(vm: IForthVM) {
        val stackTraceElements = Thread.currentThread().stackTrace
        vm.io.warning("Stack trace:")
        stackTraceElements.forEach {
            vm.io.println("    $it")
        }
    }

    /**  `.dict` ( -- : list all words with internal info )
     */
    fun w_dotDict(vm: IForthVM) {
        vm.io.println()
        for (i in 0..<vm.dict.size) {
            val w = vm.dict[i]
            vm.io.info((vm.dict as Dict).getHeaderStr(w))
        }
        vm.io.muted(Dict.Companion.HEADER_STR)
    }

    /** `.SIMILAR` ( -- ) Find similar words */

    fun w_dotSimilar(vm: IForthVM) {
        val term = vm.source.scanner.parseName().strFromAddrLen(vm).lowercase()

        vm.io.println(
            vm.dict.words
                .filter { it.name.contains(term) }
                .joinToString(" ") { it.name }.wrap(vm.io.termWidth),
        )
    }

    /** `./WORDS` ( -- n ) Get number of words */

    fun w_dotSlashWords(vm: IForthVM) {
        vm.dstk.push(vm.dict.size)
    }

    /** `.CS` ( ??? --- ) show and clear data stack */

    fun w_dotCS(vm: IForthVM) {
        vm.dstk.simpleDump()
        (vm.dstk as FStack).reset()
    }

    fun w_dotHistory(vm: IForthVM) {
        vm.io.showHistory()
    }

    fun w_dotLess(vm: IForthVM) {
        val fname = vm.source.scanner.parseName().strFromAddrLen(vm)
        val path = java.nio.file.Path.of(fname)
//        val term = vm.io.term as Terminal

        // fixme: not working
//        term.enterRawMode()
//        less(
//            term,
//            term.input(),
//            System.out,
//            System.err,
//            path,
//            emptyArray<Any>()
//        )
    }

    fun w_dotShell(vm: IForthVM) {
        val possibleArgs = generateSequence {
            vm.source.scanner.parseName().strFromAddrLen(vm)
        }
            .take(8)
            .filter { it.isNotEmpty() }
            .toList()
            .toTypedArray()
        val out = try {
            exec(false, *possibleArgs)
        } catch (e: IOException) {
            vm.io.danger("Exception: ${e.message}")
            return
        }
        vm.io.print(out)
    }

    fun w_tildeTilde(vm: IForthVM) {
        vm.io.muted("${vm.source} ${vm.dstk.simpleDumpStr()}")
    }

    fun w_dotReRun(vm: IForthVM) {
        val prev = vm.source.scanner.parseName().strFromAddrLen(vm)
        val num = try {
            prev.toInt()
        } catch (e: NumberFormatException) {
            throw ParseError("Invalid number: $prev")
        }
        val command = vm.io.runFromHistory(num)
        if (command == null) {
            throw ParseError("No history entry for: $prev")
        } else {
            vm.source.scanner.fill(command)
            vm.io.muted("Executing: $command")
        }
    }

    fun w_dotTermInfo(vm: IForthVM) {
        vm.io.termInfo()
    }
}