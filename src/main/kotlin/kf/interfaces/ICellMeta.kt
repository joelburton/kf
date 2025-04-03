package kf.interfaces


interface ICellMeta {
    fun getExplanation(vm: IForthVM, v: Int, k: Int): String
}