package kf.mem

import kf.ForthVM
import kf.mem.CellMeta
import kf.D
import kf.MemError


// ***************************************************** Adding to VM memory

/**  Append to the code section.
 *
 * All the other "appendXXXX" names call this.
 * */
fun ForthVM.appendCode(v: Int, cellMetaVal: CellMeta) {
    if (D) dbg(4, "vm.appendText: $v $cellMetaVal")
    if (cend > memConfig.codeEnd) throw MemError("Code buffer overflow")

    mem[cend] = v
    cellMeta[cend] = cellMetaVal
    cend += 1
}

/**  Append word to the code section.
 *
 * This is just a convenience function for "appendCode", as this can be
 * passed the word name, and it will find the wn and add the meta info.
 */
fun ForthVM.appendWord(s: String) {
    if (D) dbg(4, "vm.appendWord: $s")
    val wn = dict[s].wn
    appendCode(wn, CellMeta.WordNum)
}

/**  Append lit string ("lit" + len + chars) to the code section */
fun ForthVM.appendStr(s: String) {
    if (D) dbg(3, "vm.appendStr: $s")

    appendWord("lit-string")
    appendCode(s.length, CellMeta.StringLen)
    for (c in s) mem[cend++] = c.code
}

/**  Append lit counted string ("lit" + chars) to the code section */
fun ForthVM.appendCStr(s: String) {
    if (D) dbg(3, "vm.appendCStr: $s")

    appendWord("lit-string")
    // fixme: I don't think this will work; we're missing the count!
    for (c in s) mem[cend++] = c.code
}

/**  Append jump + loc to the code section */
fun ForthVM.appendJump(s: String, addr: Int) {
    appendWord(s)
    appendCode(addr, CellMeta.JumpLoc)
}

/** Append "lit" + value to code section */
fun ForthVM.appendLit(v: Int) {
    appendWord("lit")
    appendCode(v, CellMeta.NumLit)
}

// ************************************************* adding to data section

/**  Append string to the data section
 *
 * This is just the string; unlike adding to CODE, there's no LIT-STRING
 * preceding it, since it won't be executed.
 **/
fun ForthVM.appendStrToData(s: String): Int {
    if (D) dbg(3, "vm.appendStrToData: $s")
    val startAddr: Int = dend
    cellMeta[startAddr] = CellMeta.StringLen
    mem[dend++] = s.length

    for (c in s) mem[dend++] = c.code
    return startAddr + 1
}

/** Append counted string to the data section.
 *
 * Same thing (all strings are stored "counted"), but this returns the
 * address of the counted-string (ie, len+chars), rather than the addr of
 * the chars.
 * */
fun ForthVM.appendCStrToData(s: String): Int {
    return appendStrToData(s) - 1
}