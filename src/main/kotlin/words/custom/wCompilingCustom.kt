package kf.words.custom

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.warning
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.WordClass
import kf.strFromAddrLen

object wCompilingCustom : IWordClass {
    override val name = "Compiling"
    override val description = "Compiling custom words"

    override val words get() = arrayOf(
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
    fun w_recursive(vm: ForthVM) {
        val w: Word = vm.dict.currentlyDefining!!
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

    fun w_postpone(vm: ForthVM) {
        val token: String =  vm.interp.scanner.parseName().strFromAddrLen(vm)
        val w = vm.dict[token]
        val cw = vm.dict.last

        if (!cw.imm) {
            vm.io.warning(
                """Using postpone in a word not already immediate word: '$cw'.
This is almost certainly not what you want to do.""",
                whitespace = Whitespace.NORMAL,
                overflowWrap = OverflowWrap.NORMAL)
        }
        vm.appendWord("[compile]")
        vm.appendWord(token)
    }

}