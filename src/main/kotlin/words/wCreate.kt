package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.primitives.WDoes.w_addr
import kf.w_notImpl

object wCreate: IWordClass {
    override val name = "Create"
    override val description = "Data allocation and DOES"

    override val words
        get() = arrayOf(
            Word("CREATE", ::w_create),
            Word("DOES", ::w_notImpl),
            Word("ALLOT", ::w_notImpl),
        )

    /**
     * CREATE
     * CORE
     * ( "<spaces>name" -- )
     * Skip leading space delimiters. Parse name delimited by a space. Create a definition for name with the execution semantics defined below. If the data-space pointer is not aligned, reserve enough data space to align it. The new data-space pointer defines name's data field. CREATE does not allocate data space in name's data field.
     *
     * name Execution:
     * ( -- a-addr )
     * a-addr is the address of name's data field. The execution semantics of name may be extended by using DOES>.
     */

    fun w_create(vm: ForthVM) {
        val name: String = vm.interp.getToken()
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            fn = ::w_addr)
        vm.dict.add(w)
    }

}