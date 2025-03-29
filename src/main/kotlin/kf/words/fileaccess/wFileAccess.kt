package kf.words.fileaccess

import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word
import kf.strFromAddrLen
import kf.dict.w_notImpl

object wFileAccess : IWordModule {
    override val name = "kf.words.fileaccess.wFileAccess"
    override val description = "File Access"
    override val words = arrayOf<Word>(
        Word(" BIN", ::w_notImpl),
        Word("CLOSE-FILE", ::w_notImpl),
        Word("CREATE-FILE", ::w_notImpl),
        Word("DELETE-FILE", ::w_notImpl),
        Word("FILE-POSITION", ::w_notImpl),
        Word("FILE-SIZE", ::w_notImpl),
        Word("INCLUDE-FILE", ::w_notImpl),
        Word("INCLUDED", ::w_included),
        Word("OPEN-FILE", ::w_notImpl),
        Word("R/O", ::w_notImpl),
        Word("R/W", ::w_notImpl),
        Word("READ-FILE", ::w_notImpl),
        Word("READ-LINE", ::w_notImpl),
        Word("REPOSITION-FILE", ::w_notImpl),
        Word("RESIZE-FILE", ::w_notImpl),
//Word("S\"", ::w_notImpl),
//        Word("SOURCE-ID", ::w_notImpl),
        Word("W/O", ::w_notImpl),
        Word("WRITE-FILE", ::w_notImpl),
        Word("WRITE-LINE", ::w_notImpl),

        )

    /**
     * `INCLUDE-FILE`( i * x fileid -- j * x ) Include file from fileId
     *
     * Remove fileid from the stack. Save the current input source
     * specification, including the current value of SOURCE-ID. Store fileid in
     * SOURCE-ID. Make the file specified by fileid the input source. Store zero
     * in BLK. Other stack effects are due to the words included.
     *
     * Repeat until end of file: read a line from the file, fill the input
     * buffer from the contents of that line, set >IN to zero, and interpret.
     *
     * Text interpretation begins at the file position where the next file read
     * would occur.
     *
     * When the end of the file is reached, close the file and restore the input
     * source specification to its saved value.
     *
     * An ambiguous condition exists if fileid is invalid, if there is an I/O
     * exception reading fileid, or if an I/O exception occurs while closing
     * fileid. When an ambiguous condition exists, the status (open or closed)
     * of any files that were being interpreted is implementation-defined.
     */

    fun w_includeFile(vm: ForthVM) {

    }

    /** `INCLUDED` ( i * x c-addr u -- j * x ) Include file (c-addr + u) */

    fun w_included(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val path = Pair(addr, len).strFromAddrLen(vm)
        wFileAccessExt.include(vm, path)
    }
}