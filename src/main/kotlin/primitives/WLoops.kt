package kf.primitives

import kf.D
import kf.ForthVM
import kf.Word
import kf.WordClass

object WLoops : WordClass {
    override val name = "Loops"
    override val primitives
        get() = arrayOf(
            Word("begin", ::w_begin, imm = true, compO = true),
            Word("again", ::w_again, imm = true, compO = true),

            Word("until", ::w_until, imm = true, compO = true),
            Word("while", ::w_while, imm = true, compO = true),
            Word("repeat", ::w_repeat, imm = true, compO = true),

            Word("do", ::w_do, imm = true, compO = true),
            Word("(do)", ::w_parenDo, compO = true, hidden = true),
            Word("loop", ::w_loop, imm = true, compO = true),
            Word("i", ::w_i, compO = true),
            Word("j", ::w_j, compO = true),
            Word("k", ::w_k, compO = true),

            Word("(loop)", ::w_parenLoop, compO = true, hidden = true),

            Word("leave", ::w_leave, imm = true, compO = true),
            Word("unloop", ::w_leave, imm = true, compO = true),

            // -do down-counting
        )


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

    fun w_begin(vm: ForthVM) {
        vm.dstk.push(vm.cend)
    }

    /**  again
     *
     * BEGIN ... AGAIN
     *
     * This is immediate; it just finds the address of the start of the loop
     * and adds the jump to there to the definition.
     * */

    fun w_again(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

    /**  until
     *
     * BEGIN ... f UNTIL
     *
     * Same as again, but the jump written out for runtime is a conditional one.
     * */

    fun w_until(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("0branch", bwref)
    }

    /** BEGIN .a. WHILE .b. REPEAT
     *
     * (there must be exactly one WHILE clause(
     *
     * Add a placeholder branch, and put this address on the stack for REPEAT
     * to fix the jump.
     */

    private fun w_while(vm: ForthVM) {
        vm.appendJump("0branch", 0xffff)
        vm.dstk.push(vm.cend) // location of while
    }

    /** BEGIN .a. WHILE .b. REPEAT
     *
     * Fix the address of the WHILE so that it can jump to the right after
     * the REPEAT statement. Pull the BEGIN address and write out a jump
     * to it.
     *
     */
    private fun w_repeat(vm: ForthVM) {
        val whileRef = vm.dstk.pop()
        vm.mem[whileRef - 1] = vm.cend + 2 // failing while goes past me
        val bwref = vm.dstk.pop()
        vm.appendJump("branch", bwref)
    }

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

    fun w_do(vm: ForthVM) {
        vm.appendWord("(do)")
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us
        vm.rstk.push(0)  // how many forward refs we'll need to fix (LEAVE)L
    }

    /** This is the runtime part --- move the limit/start vars to the Lstack */

    fun w_parenDo(vm: ForthVM) {
        vm.rstk.push(vm.dstk.pop())
        vm.rstk.push(vm.dstk.pop())
    }

    /** LEAVE leaves a DO loop (there's no flag or such, but you can put it in
     * a normal IF condition). This is the immediate part.
     *
     * It pushes its address beneath the "count of how many refs need to be
     * fixed?" and bumps the number up.
     */

    private fun w_leave(vm: ForthVM) {
        vm.appendWord("L>")
        vm.appendWord("L>")
        vm.appendWord("2drop")
        vm.appendJump("branch", 0xff)
        val count = vm.rstk.pop()
        vm.rstk.push(vm.cend - 1, count + 1)
    }

    /** The LOOP part has both immediate and runtime parts.
     *
     * At compilation time, this fixes all of the LEAVE jumps that have been
     * gathered. It also adds the (LOOP) function to the code for runtime.
     */

    /**  loop */
    fun w_loop(vm: ForthVM) {
        val numExits = vm.rstk.pop()
        vm.appendJump("(loop)", vm.rstk.popFrom(numExits))
        repeat(numExits) { vm.mem[vm.rstk.pop()] = vm.cend }
    }


    /** (LOOP): the runtime part */

    fun w_parenLoop(vm: ForthVM) {  // dstk is index limit incby
        runtimeLoop(vm, 1)
    }



    // Both of those use this internal func; it runs at runtime:
    //
    // - bump if index
    // - done looping: skip past the jump-address for start of loop
    // - more looping: put the index/limit back on stack and loop
    fun runtimeLoop(vm: ForthVM, incrementBy: Int) {
        val limit = vm.rstk.pop()
        val index = vm.rstk.pop() + incrementBy
        if (index >= limit) {
            if (D) vm.dbg(3, "w_loopImpl: done")
            vm.ip += 1  // skip the jump-addr after (loop)
        } else {
            if (D) vm.dbg(3, "w_loopImpl: looping")
            vm.rstk.push(index, limit)  // push limit/index back on
            vm.ip = vm.mem[vm.ip]  // jump to start of loop
        }
    }

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

    private fun w_i(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(1))
    }

    private fun w_j(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(3))
    }

    private fun w_k(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(5))
    }

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

    private fun w_unloop(vm: ForthVM) {
        vm.rstk.pop()
        vm.rstk.pop()
    }

} // : *begin .cend @ . .cend @ immediate ;
// : *again 'z' emit ['] branch ,, ,, immediate ;  : test 10 *begin 'A' emit 1- dup 0= if return then *again ; test