package kf.interps

import kf.dict.IWordMetaModule
import kf.words.mForthInterp

/** The "classic" Forth interpreter.
 *
 * The interpreter program here is much longer and broken down into more
 * atomic, classic Forth words (`FIND`, `WORD`, etc.)
 *
 * It will run a bit slower (not the VM when running code, just the REPL),
 * but in debugging mode will spew obnoxious amounts of info, since dozens
 * of Forth works are executed for even a simple line entered to evaluate.
 *
 * However, it relies on far less specialized high-level words, and is closer
 * to the Forth ethos of build-Forth-from-Forth.
 *
 * Interesting note:  the list of required words in COLD-RAW mode here is much
 * longer, since it uses lots of Forth words (BL, DUP, ?DUP, STATE, >NUMBER,
 * and many others) that [InterpFast] don't need since the logic those words
 * supply is handled by the big interp-a-token word it uses.
 *
 */

class InterpTraditional() : InterpEval() {
    override val name = "Traditional"
    override val module: IWordMetaModule = mForthInterp
    // fun fact: at the time I wrote this, it was the longest Forth program
    // I had ever written ;-)
    override val code =  """
    begin 
      refill while                        ( loop as long as get-a-line succeeds
        begin
          bl word                         ( char "<chars>ccc<char>" -- c-addr )  
          dup @ while                     ( loop until word-len 0 found (EOL)
            state @ if                    ( compile
              find ?dup if                ( find returns 1=imm, -1=compile
                1 = if execute
                else compile,
                then
              else                        ( find returned 0, parse as int
                count 0 0 2swap >number 
                drop drop drop            ( drop all but actual num found
                ['] lit compile, ,,       ( compile lit int 
              then
            else                          ( interpreting
              find if
                execute                   ( call the word
              else                        ( couldn't find, try as number
                count 0 0 2swap >number 
                drop drop drop
              then
            then
          repeat
        drop
        3 spaces 111 emit 107 emit        ( "ok"
        45 emit depth . cr
      repeat                              ( end of loop if get-a-line fails
    eof
        """
}