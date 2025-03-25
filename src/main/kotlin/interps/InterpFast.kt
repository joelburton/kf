package kf.interps

import kf.ForthVM
import kf.Word
import kf.words.mFastInterp

/** A nice, straightforward interpreter and probably should be the default.
 *
 * It's called "fast" because the interpreter itself doesn't have a lot of
 * Forth instructions; instead, there are Kotlin-written pieces that do
 * several tasks that would classically be separate Forth words.
 *
 * This doesn't make the VM faster when it's running your code (the interpreter
 * isn't involved in that), but it makes the REPL faster and, more importantly,
 * easier to work with in debugging mode, since there's far less output
 * for each Forth token because there are far-fewer.
 *
 * This uses mostly ANS standard words except the big one:
 * INTERP-PROCESS-TOKEN.
 *
 * An alternative to this is the [InterForth], which is written in Forth,
 * and doesn't use newfangled words and sticks with the classics.
 */

class InterpFast(vm: ForthVM) : InterpEval(vm) {
    override val name = "Fast"
    override val module = mFastInterp
    override val code = """
        begin 
            interp-prompt
            refill while
                begin
                    parse-name dup while
                        interp-process-token
                    repeat
                    2drop
            repeat
        eof
        """

    override fun reboot() {
        vm.dict.add(Word("INTERP-PROCESS-TOKEN", ::w_processToken))
        super.reboot()
    }

}