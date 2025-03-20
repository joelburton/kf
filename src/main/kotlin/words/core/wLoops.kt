package kf.words.core

import kf.D
import kf.ForthVM
import kf.IWordClass
import kf.Word

object wLoops: IWordClass {
    override val name = "Loops"
    override val description = "Looping words"

    override val words
        get() = arrayOf(
            Word("(+LOOP)", ::w_parenPlusLoop, compO = true, hidden = true),
            Word("+LOOP", ::w_plusLoop, imm = true, compO = true),

            Word("BEGIN", ::w_begin, imm = true, compO = true),

            Word("UNTIL", ::w_until, imm = true, compO = true),
            Word("WHILE", ::w_while, imm = true, compO = true),
            Word("REPEAT", ::w_repeat, imm = true, compO = true),

            Word("DO", ::w_do, imm = true, compO = true),
            Word("(DO)", ::w_parenDo, compO = true, hidden = true),
            Word("LOOP", ::w_loop, imm = true, compO = true),
            Word("I", ::w_i, compO = true),
            Word("J", ::w_j, compO = true),
//            Word("K", ::w_k, compO = true),

            Word("(LOOP)", ::w_parenLoop, compO = true, hidden = true),

            Word("LEAVE", ::w_leave, imm = true, compO = true),
            Word("UNLOOP", ::w_leave, imm = true, compO = true),
        )

    /** +LOOP
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: do-sys -- )
     * Append the run-time semantics given below to the current definition. Resolve the destination of all unresolved occurrences of LEAVE between the location given by do-sys and the next location for a transfer of control, to execute the words following +LOOP.
     *
     * Run-time:
     * ( n -- ) ( R: loop-sys1 -- | loop-sys2 )
     * An ambiguous condition exists if the loop control parameters are unavailable. Add n to the loop index. If the loop index did not cross the boundary between the loop limit minus one and the loop limit, continue execution at the beginning of the loop. Otherwise, discard the current loop control parameters and continue execution immediately following the loop.
     */

    /** The +LOOP part has both immediate and runtime parts.
     *
     * 100% the same, but writes out (+loop) into the code.
     */

    fun w_plusLoop(vm: ForthVM) {
        val numExits = vm.rstk.pop()
        vm.appendJump("(+loop)", vm.rstk.popFrom(numExits))
        repeat(numExits) { vm.mem[vm.rstk.pop()] = vm.cend }
    }

    /** (+LOOP): the runtime part */

    fun w_parenPlusLoop(vm: ForthVM) {  // dstk is index limit incby
        runtimeLoop(vm, vm.dstk.pop())
    }


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


    /**  until
     *
     * BEGIN ... f UNTIL
     *
     * Same as again, but the jump written out for runtime is a conditional one.
     * */

    fun w_until(vm: ForthVM) {
        val bwref = vm.dstk.pop()
        vm.appendJump("0branch-abs", bwref)
    }

    /** BEGIN .a. WHILE .b. REPEAT
     *
     * (there must be exactly one WHILE clause(
     *
     * Add a placeholder branch, and put this address on the stack for REPEAT
     * to fix the jump.
     */

    private fun w_while(vm: ForthVM) {
        vm.appendJump("0branch-abs", 0xffff)
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
        vm.appendJump("branch-abs", bwref)
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
        vm.appendJump("branch-abs", 0xff)
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
     *   index  <- k
     */

    private fun w_i(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(1))
    }

    private fun w_j(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(3))
    }

//    private fun w_k(vm: ForthVM) {
//        vm.dstk.push(vm.rstk.getFrom(5))
//    }

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

    /**
     * 6.1.1680
     * I
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Execution:
     * ( -- n | u ) ( R: loop-sys -- loop-sys )
     * n | u is a copy of the current (innermost) loop index. An ambiguous condition exists if the loop control parameters are unavailable.
     */

    /**
     * J
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Execution:
     * ( -- n | u ) ( R: loop-sys1 loop-sys2 -- loop-sys1 loop-sys2 )
     * n | u is a copy of the next-outer loop index. An ambiguous condition exists if the loop control parameters of the next-outer loop, loop-sys1, are unavailable.
     *
     */

    /**
     * DO
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: -- do-sys )
     * Place do-sys onto the control-flow stack. Append the run-time semantics given below to the current definition. The semantics are incomplete until resolved by a consumer of do-sys such as LOOP.
     *
     * Run-time:
     * ( n1 | u1 n2 | u2 -- ) ( R: -- loop-sys )
     * Set up loop control parameters with index n2 | u2 and limit n1 | u1. An ambiguous condition exists if n1 | u1 and n2 | u2 are not both the same type. Anything already on the return stack becomes unavailable until the loop-control parameters are discarded.
     *
     *
     */

    /**
     * LOOP
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: do-sys -- )
     * Append the run-time semantics given below to the current definition. Resolve the destination of all unresolved occurrences of LEAVE between the location given by do-sys and the next location for a transfer of control, to execute the words following the LOOP.
     *
     * Run-time:
     * ( -- ) ( R: loop-sys1 -- | loop-sys2 )
     * An ambiguous condition exists if the loop control parameters are unavailable. Add one to the loop index. If the loop index is then equal to the loop limit, discard the loop parameters and continue execution immediately following the loop. Otherwise continue execution at the beginning of the loop.
     */

    /**
     * 6.1.2380
     * UNLOOP
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Execution:
     * ( -- ) ( R: loop-sys -- )
     * Discard the loop-control parameters for the current nesting level. An UNLOOP is required for each nesting level before the definition may be EXITed. An ambiguous condition exists if the loop-control parameters are unavailable.
     */

    /**
     * UNTIL
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: dest -- )
     * Append the run-time semantics given below to the current definition, resolving the backward reference dest.
     *
     * Run-time:
     * ( x -- )
     * If all bits of x are zero, continue execution at the location specified by dest.
     */

    /**
     * WHILE
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: dest -- orig dest )
     * Put the location of a new unresolved forward reference orig onto the control flow stack, under the existing dest. Append the run-time semantics given below to the current definition. The semantics are incomplete until orig and dest are resolved (e.g., by REPEAT).
     *
     * Run-time:
     * ( x -- )
     * If all bits of x are zero, continue execution at the location specified by the resolution of orig.
     */

    /**
     * BEGIN
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: -- dest )
     * Put the next location for a transfer of control, dest, onto the control flow stack. Append the run-time semantics given below to the current definition.
     *
     * Run-time:
     * ( -- )
     * Continue execution.
     *
     *
     */

    /**
     * REPEAT
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Compilation:
     * ( C: orig dest -- )
     * Append the run-time semantics given below to the current definition, resolving the backward reference dest. Resolve the forward reference orig using the location following the appended run-time semantics.
     *
     * Run-time:
     * ( -- )
     * Continue execution at the location given by dest.
     */

    /**
     * LEAVE
     * CORE
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     * Execution:
     * ( -- ) ( R: loop-sys -- )
     * Discard the current loop control parameters. An ambiguous condition exists if they are unavailable. Continue execution immediately following the innermost syntactically enclosing DO...LOOP or DO...+LOOP.
     */
}