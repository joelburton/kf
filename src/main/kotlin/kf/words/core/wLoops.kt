package kf.words.core

import kf.D
import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word
import kf.mem.appendJump
import kf.mem.appendWord

object wLoops : IWordModule {
    override val name = "kf.words.core.wLoops"
    override val description = "Looping words"

    override val words
        get() = arrayOf(
            // BEGIN...UNTIL
            // BEGIN...WHILE...REPEAT
            // BEGIN...AGAIN (not here, "AGAIN" is in wLoopsExt)
            Word("BEGIN", ::w_begin, imm = true, compO = true),
            Word("UNTIL", ::w_until, imm = true, compO = true),
            Word("WHILE", ::w_while, imm = true, compO = true),
            Word("REPEAT", ::w_repeat, imm = true, compO = true),

            // DO...LOOP   and  DO...LEAVE...LEAVE...LOOP
            // DO...+LOOP  and  DO...LEAVE...LEAVE...+LOOP
            Word("DO", ::w_do, imm = true, compO = true),
            Word("(DO)", ::w_parenDo, compO = true, hidden = true),

            Word("LOOP", ::w_loop, imm = true, compO = true),
            Word("(LOOP)", ::w_parenLoop, compO = true, hidden = true),

            Word("+LOOP", ::w_plusLoop, imm = true, compO = true),
            Word("(+LOOP)", ::w_parenPlusLoop, compO = true, hidden = true),

            Word("I", ::w_i, compO = true),
            Word("J", ::w_j, compO = true),

            Word("LEAVE", ::w_leave, imm = true, compO = true),
            Word("(LEAVE)", ::w_parenLeave, compO = true),
            Word("UNLOOP", ::w_unloop, compO = true),
        )


    // *************************************************************************
    //
    // BEGIN loops (all 3 types) are much simpler to code, and have no runtime
    // components -- everything is written out to the code.
    //
    // DO loops have a mix of immediate and runtime code; the runtime code
    // have names like (FOO).
    //
    // "AGAIN" is an extension, and so is defined there.

    /**  `BEGIN` IM CO (C: -- addr )
     *
     * BEGIN ... AGAIN : loops forever
     * BEGIN ... f UNTIL : loops until f is true
     * BEGIN .a. f WHILE .b. REPEAT : does `a`, if f true:`b`+loops, else:end
     * ^^^^^
     *
     * This is an immediate word; it has no runtime component. It just
     * puts the start-loop address on the stack-during-compilation, so at
     * later points in the loop-creation, the addr can be found.
     * */

    fun w_begin(vm: ForthVM) {
        vm.rstk.push(vm.cend)
    }

    /** UNTIL IM CO (C: addr -- )
     *
     * BEGIN ... f UNTIL
     *             ^^^^^
     * Same as again, but jump written out for runtime is a conditional one.
     * */

    fun w_until(vm: ForthVM) {
        val bwref = vm.rstk.pop()
        vm.appendJump("0branch", bwref - vm.cend - 1)
    }

    /** WHILE (C: -- addr)
     *
     * BEGIN .a. WHILE .b. REPEAT
     *           ^^^^^
     * (there must be exactly one WHILE clause)
     *
     * Add a placeholder branch, and put this address on the stack for REPEAT
     * to fix the jump.
     */

    fun w_while(vm: ForthVM) {
        vm.appendJump("0branch", 0xffff)
        vm.rstk.push(vm.cend) // location of while
    }

    /** BEGIN .a. WHILE .b. REPEAT
     *                      ^^^^^^
     * Fix the address of the WHILE so that it can jump to the right after
     * the REPEAT statement. Pull the BEGIN address and write out a jump
     * to it.
     *
     */
    fun w_repeat(vm: ForthVM) {
        val whileRef = vm.rstk.pop()
        val bwref = vm.rstk.pop()
        vm.appendJump("branch", (bwref - vm.cend) - 1)
        vm.mem[whileRef - 1] = (vm.cend - whileRef) + 1
    }


    // ********************************************************************** DO

    /** `DO` IM CO (C: -- addr 0 ) Start loop compilation */

    fun w_do(vm: ForthVM) {
        vm.appendWord("(DO)")
        vm.rstk.push(vm.cend) // start of do loop, so end can come back to us
        vm.rstk.push(0)  // how many forward refs we'll need to fix (LEAVE)L
    }

    /** This is the runtime part --- move the limit/start vars to the Lstack */

    fun w_parenDo(vm: ForthVM) {
        vm.rstk.push(vm.dstk.pop(), vm.dstk.pop()) // limit, start
    }


    // ********************************************************** LOOP and +LOOP

    // Both (LOOP) and (+LOOP) use this internal func:
    //
    // - bump index by increment-amount
    // - done looping: skip past the jump-address for start of loop
    // - more looping: put the index/limit back on stack and loop

    private fun runtimeLoop(vm: ForthVM, incrementBy: Int) {
        val limit = vm.rstk.pop()
        val index = vm.rstk.pop() + incrementBy
        if (index >= limit) {
            if (D) vm.dbg(3, "runtimeLoop: done")
            vm.ip += 1  // skip the jump-addr after (loop)
        } else {
            if (D) vm.dbg(3, "runtimeLoop: continuing loop")
            vm.rstk.push(index, limit)  // push limit/index back on
            vm.ip += vm.mem[vm.ip]  // jump to start of loop
        }
    }


    /** `LOOP` IM CO (C: addr * exits n -- ) Compile part of LOOP
     *
     * This is the immediate part. It stashes the loop-start-addr on rstk
     * and also 0 zero for "how many refs need to be fixed up?". Each LEAVE
     * will add another address and bump that number up.
     *
     * */

    fun w_loop(vm: ForthVM) {
        val numExits = vm.rstk.pop()
        vm.appendJump("(LOOP)", vm.rstk.popFrom(numExits) - vm.cend - 1)

        for (i in numExits downTo 1) {
            val bwRef = vm.rstk.pop()
            vm.mem[bwRef] = vm.cend - bwRef
        }
    }

    /** (LOOP) CO : the runtime part */

    fun w_parenLoop(vm: ForthVM) {  // dstk is index limit incby
        runtimeLoop(vm, incrementBy = 1)
    }

    /** `+LOOP` IM CO (C: addr * exits n -- ) Compile part of +LOOP
     *
     * This is the immediate part. It stashes the loop-start-addr on rstk
     * and also 0 zero for "how many refs need to be fixed up?". Each LEAVE
     * will add another address and bump that number up.
     *
     * */

    fun w_plusLoop(vm: ForthVM) {
        val numExits = vm.rstk.pop()
        vm.appendJump("(+LOOP)", vm.rstk.popFrom(numExits) - vm.cend - 1)
        for (i in numExits downTo 1) {
            val bwRef = vm.rstk.pop()
            vm.mem[bwRef] = vm.cend - bwRef
        }
    }

    /** (+LOOP): the runtime part */

    fun w_parenPlusLoop(vm: ForthVM) {  // dstk is index limit incby
        runtimeLoop(vm, incrementBy = vm.dstk.pop())
    }


    // i j : these are subtle, and work differently than other languages.
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
     */

    fun w_i(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(1))
    }

    fun w_j(vm: ForthVM) {
        vm.dstk.push(vm.rstk.getFrom(3))
    }

    /** `LEAVE` IM CO (C: n -- n ) leaves a DO loop
     *
     * There's no flag or such, but you can put it in
     * a normal IF condition. This is the immediate part.
     *
     * It pushes its address beneath the "count of how many refs need to be
     * fixed?" and bumps the number up.
     */

    fun w_leave(vm: ForthVM) {
        // should this be a subordinate word like "(LEAVE)" ?
//        vm.appendWord("R>")
//        vm.appendWord("R>")
//        vm.appendWord("2drop")
        vm.appendJump("(leave)", 0xffff)
        val count = vm.rstk.pop()
        vm.rstk.push(vm.cend - 1, count + 1)
    }

    fun w_parenLeave(vm: ForthVM) {
        vm.rstk.pop()
        vm.rstk.pop()
        vm.ip = vm.ip + vm.mem[vm.ip]
    }


    /** `UNLOOP` (C: addr addr -- ) Remove one level of DO loop vars
     *
     * This is needed if you exit a function directly while in a loop:
     *
     *    10 0 DO SOME-FLAG IF UNLOOP EXIT THEN ... LOOP
     *
     * If you're in two loops, you'll need 2 "UNLOOP"s:
     *
     *   10 0 DO 20 0 DO SOME-FLAG IF UNLOOP UNLOOP EXIT THEN ... LOOP LOOP
     *
     */

    fun w_unloop(vm: ForthVM) {
        vm.rstk.pop()
        vm.rstk.pop()
    }
}