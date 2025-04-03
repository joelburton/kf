package kf.words.core

import kf.dict.Word
import kf.interfaces.FALSE
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.strFromAddrLen


object wSystem: IWordModule {
    override val name = "kf.words.core.wSystem"
    override val description = "The system outside of the VM"

    override val words
        get() = arrayOf<Word>(
            Word("ENVIRONMENT?", ::w_environment),
        )

    /** `ENVIRONMENT` ( c-addr u -- false | i * x true ) Query environ
     *
     * Given the string from c-addr and u, responds to the query:
     *
     * - if the string is unknown, flag=false
     * - else, flag is true and query-specific things are pushed before that
     *
     */

    // COUNTED-STRING	n	yes	maximum size of a counted string, in characters
    ///HOLD	n	yes	size of the pictured numeric output string buffer, in characters
    ///PAD	n	yes	size of the scratch area pointed to by PAD, in characters
    //ADDRESS-UNIT-BITS	n	yes	size of one address unit, in bits
    //FLOORED	flag	yes	true if floored division is the default
    //MAX-CHAR	u	yes	maximum value of any character in the implementation-defined character set
    //MAX-D	d	yes	largest usable signed double number
    //MAX-N	n	yes	largest usable signed integer
    //MAX-U	u	yes	largest usable unsigned integer
    //MAX-UD	ud	yes	largest usable unsigned double number
    //RETURN-STACK-CELLS	n	yes	maximum size of the return stack, in cells
    //STACK-CELLS	n	yes	maximum size of the data stack, in cells

    fun w_environment(vm: IForthVM) {
        val s = vm.source.scanner.parseName().strFromAddrLen(vm)

        when (s.lowercase()) {
            "/counted-string" -> vm.dstk.push(
                vm.memConfig.scratchEnd - vm.memConfig.scratchStart)
            "/hold" -> vm.dstk.push(
                vm.memConfig.scratchEnd - vm.memConfig.scratchStart)
            "/pad" -> vm.dstk.push(vm.memConfig.padEnd - vm.memConfig.padStart)
            "address-unit-bits" -> vm.dstk.push(32)
            "floored" -> vm.dstk.push(FALSE)
            "max-char" -> vm.dstk.push(0xFFFF)
            "max-d" -> vm.dstk.push(Int.MAX_VALUE)
            "max-n" -> vm.dstk.push(Int.MAX_VALUE)
            "max-u" -> vm.dstk.push(Int.MAX_VALUE)
            "max-ud" -> vm.dstk.dblPush(Int.MAX_VALUE.toLong())
            "return-stack-cells" -> vm.dstk.push(
                vm.memConfig.rstackEnd - vm.memConfig.rstackStart)
            "stack-cells" -> vm.dstk.push(
                vm.memConfig.dstackEnd - vm.memConfig.dstackStart)
            else -> vm.dstk.push(FALSE)
        }
    }

}