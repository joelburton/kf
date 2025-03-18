package kf.interps

import kf.ForthVM

class InterpForth(vm: ForthVM) : InterpCLI(vm) {
    override val name = "Forth"
    override val code =  """
        begin 
            refill while
                begin
                    word dup @ while
                        state @ if
                            find ?dup if
                                1 = if
                                    execute
                                else
                                    ,,
                                then
                            else
                                count 0 0 2swap >number drop drop drop
                                do-lit ,, ,,
                            then
                        else
                            find if
                                execute
                            else
                                count 0 0 2swap >number drop drop drop
                            then
                        then
                    repeat
                drop
                3 spaces        
                111 emit 107 emit
                cr
            repeat
        eof
        """
}