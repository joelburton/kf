package kf.interfaces

import kf.ForthVM

interface ICellMeta {
    fun getExplanation(vm: IForthVM, v: Int, k: Int): String
}