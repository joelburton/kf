package kf.interps

import kf.ForthVM

class InterpForth(vm: ForthVM) : InterpCLI(vm) {
    override val name = "Forth"
    override val code =  """
    begin 
      refill while                        \ loop as long as get-a-line succeeds
        begin
          bl word                         ( char "<chars>ccc<char>" -- c-addr )  
          dup @ while                     \ loop until word-len 0 found (EOL)
            state @ if                    \ compile
              find ?dup if                \ find returns 1=imm, -1=compile
                1 = if execute
                else compile,
                then
              else                        \ find returned 0, parse as int
                count 0 0 2swap >number 
                drop drop drop            \ drop all but actual num found
                ['] lit compile, ,,       \ compile lit int 
              then
            else                          \ interpreting
              find if
                execute                   \ call the word
              else                        \ couldn't find, try as number
                count 0 0 2swap >number 
                drop drop drop
              then
            then
          repeat
        drop
        3 spaces 111 emit 107 emit        \ "ok"
        45 emit depth . cr
      repeat                              \ end of loop if get-a-line fails
    eof
        """
}