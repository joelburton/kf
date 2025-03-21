package kf.words.core.ext

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wLoopsExt : IWordClass {
    override val name = "core.ext.loopsExt"
    override val description = "Loops Extension"
    override val words
        get() = arrayOf(
            Word("AGAIN", ::w_again, imm = true, compO = true),
            Word("?DO", ::w_notImpl),
        )

    /**  again
     *
     * BEGIN ... AGAIN
     *
     * This is immediate; it just finds the address of the start of the loop
     * and adds the jump to there to the definition.
     * */



    /**  begin (a loop)
     *
     * BEGIN ... AGAIN : loops forever
     * BEGIN ... f UNTIL : loops until f is true
     * BEGIN .a. f WHILE .b. REPEAT : does a, if f true ( b, loops) else ( end )
     *
     * This is an immediate word; it has not runtime component. It just
     * puts the start-loop address on the stack-during-compilation, so at
     * later points in the loop-creation, the addr can be found.
     * */

    /**  again
     *
     * BEGIN ... AGAIN
     *
     * This is immediate; it just finds the address of the start of the loop
     * and adds the jump to there to the definition.
     * */

    fun w_again(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("branch", bwref - vm.cend - 1)
    }

    /**  until
     *
     * BEGIN ... f UNTIL
     *
     * Same as again, but the jump written out for runtime is a conditional one.
     * */

    /** BEGIN .a. WHILE .b. REPEAT
     *
     * (there must be exactly one WHILE clause(
     *
     * Add a placeholder branch, and put this address on the stack for REPEAT
     * to fix the jump.
     */

    /** BEGIN .a. WHILE .b. REPEAT
     *
     * Fix the address of the WHILE so that it can jump to the right after
     * the REPEAT statement. Pull the BEGIN address and write out a jump
     * to it.
     *
     */

    // *************************************************************************
    //
    // BEGIN loops (all 3 types) are much simpler to code, and have no runtime
    // components -- everything is written out to the code.
    //
    // DO loops have a mix of immediate and runtime code; the runtime code
    // have names like (FOO).

    /**  loop  (limit start -- limit start R:addr  )
     *
     * This is the immediate part. It stashes the loop-start-addr on rstk
     * and also 0 zero for "how many refs need to be fixed up?". Each LEAVE
     * will add another address and bump that number up.
     * */


    /** This is the runtime part --- move the limit/start vars to the Lstack */


    /** LEAVE leaves a DO loop (there's no flag or such, but you can put it in
     * a normal IF condition). This is the immediate part.
     *
     * It pushes its address beneath the "count of how many refs need to be
     * fixed?" and bumps the number up.
     */


    /** The LOOP part has both immediate and runtime parts.
     *
     * At compilation time, this fixes all of the LEAVE jumps that have been
     * gathered. It also adds the (LOOP) function to the code for runtime.
     */

    /**  loop */


    /** (LOOP): the runtime part */





    // i j k : the are subtle, and work differently than other languages.
    //
    // in most languages:
    //    for i in some range:
    //      print i          // outer loop var
    //      for j in some range:
    //        print i     // exact same: outer loop over
    //        print j     // inner loop var
    //
    // in forth, though, this is more like:
    //
    //    do
    //      i                // "this" loop var (right now, the outer)
    //      do
    //        j              // the "outer" loop (same as what i was above)
    //        i              // the "inner" loop
    //
    // This makes these easy to implement -- the "loop you're in" (i) is always
    // the top var on the LStack, your parent loop (j) is always one down,
    // and so on.
    //
    // I originally misunderstood this and had a very brain-melty time trying
    // to figure out how to easily handle i/j/k without introducing all sorts
    // of meta-state about "what depth of loop are you in in this function?",
    // but then I remembered the strange "i isn't the first loop, its the
    // one you're most immediately in" thing.

    /** Get the index of the innermost loop you're in. The LStack has
     *
     *   limit  <- top
     *   index  <- i
     *   limit
     *   index  <- j
     *   limit
     *   index  <- j
     */


    // We could have more (l, m, etc), but in Forth, very small words are
    // discouraged, so it's already a bit of a code smell to have "j", let
    // alone "k".


    /** Remove one level of DO loop vars. This is needed if you exit a function
     * directly while in a loop:
     *
     *    10 0 DO SOME-FLAG IF UNLOOP EXIT THEN ... LOOP
     *
     * If you're in two loops, you'll need 2 "UNLOOP"s:
     *
     *   10 0 DO 20 0 DO SOME-FLAG IF UNLOOP UNLOOP EXIT THEN ... LOOP LOOP
     *
     */

}