package kf.words.custom

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.mem.appendWord
import kf.strFromAddrLen

object wCompilingCustom : IWordModule {
    override val name = "kf.words.custom.wCompilingCustom"
    override val description = "Compiling custom words"

    override val words get() = arrayOf<Word>(
        Word("RECURSIVE", ::w_recursive, imm = true, compO = true),
    )

    /**  `:` X ( in:"name" -- : create word 'name' and start compiling mode )
     */


    /** `;` IC ( -- : complete word definition and exit compiling mode )`
     */

    //    ///  ( -- ) really lit ? is it ok to just use lit for this?
    //    static void w_imm_lit(ForthVM vm) {
    //        String token = PrimInterpreter.getToken(vm);
    //        int val = Utils.tryAsInt(token, vm.getBase());
    //        vm.dbg("w_imm_lit %d", val);
    //        vm.appendCode(val, CellMeta.number_literal);
    //    }
    //  : [literal] dolit ,, ,, ;   \ writes lit/stack-top
    //  : literal immediate [literal] ; \ same, but imm mode
    //  : ['] immediate ' [literal] ;


    /** `recursive` CI ( -- : from this point onward, word can recurse )
     */
    fun w_recursive(vm: IForthVM) {
        val w = vm.dict.currentlyDefining!!
        w.recursive = true
    }


    /**  `postpone` ( "w" -- : writes word into curr definition )
     *
     * Postpone is useful for postponing-evaluating a word:
     *
     * For example:
     *   : aa 'a' ;
     *   : x immediate postpone aa ;
     *   : y x x ;
     *
     *  'y' is compiled to "aa / aa / ret" (not: "x / x / ret").
     *
     *  It can also be used to alias a word:
     *    : my-if immediate postpone if ;
     *    : test 10 my-if 20 then ;
     *
     * Which compiles to the same thing as if "test" used "if" directly.
     */

    fun w_postpone(vm: IForthVM) {
        val token: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = vm.dict[token]
        val cw = vm.dict.last

        if (!cw.imm) {
            vm.io.warning("Using postpone in word not already immediate: '$cw'.")
            vm.io.warning("This is almost certainly not what you want to do.")
        }
        vm.appendWord("[compile]")
        vm.appendWord(token)
    }

}