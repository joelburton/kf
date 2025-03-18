package kf.interps

import kf.*

open class InterpCLI(vm: ForthVM) : InterpEval(vm) {
    override val name = "CLI"
}