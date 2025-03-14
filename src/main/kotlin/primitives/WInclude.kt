package kf.primitives

import com.github.ajalt.mordant.terminal.Terminal
import kf.D
import kf.ForthEOF
import kf.ForthError
import kf.ForthQuitNonInteractive
import kf.ForthVM
import kf.TerminalFileInterface
import kf.Word
import kf.WordClass

object WInclude : WordClass {
    override val name = "Include"
    override val primitives = arrayOf(
        // including new primitives and forth files
        Word("include", ::w_include),
        Word("include-primitives", ::w_includeBinary),
    )

    /**  Read in a primitive class dynamically
     */
    fun readPrimitiveClass(vm: ForthVM, name: String) {
        if (D) vm.dbg(3, "vm.readPrimitiveClass: $name")
        try {
            val cls: Class<*> = Class.forName(name)
            val mod = cls.getConstructor(ForthVM::class.java)
                .newInstance(this) as WordClass
            vm.dict.addModule(mod)
        } catch (e: Exception) {
            when (e) {
                is ClassNotFoundException,
                is IllegalAccessException,
                is NoSuchFieldException ->
                    throw ForthError("Can't load: $name (${e.message})")

                else -> throw e // Re-throw other unexpected exceptions
            }
        }
    }


    // *************************************************************************

    /**  `include` `( in:"file" -- : read Forth file in )` */

    fun w_include(vm: ForthVM) {
        val path = vm.getToken()

        val prevIO: Terminal = vm.io
        val prevVerbosity: Int = vm.verbosity

        vm.io = Terminal(terminalInterface = TerminalFileInterface(path))
        vm.verbosity = -2
        try {
            vm.runVM()
        } catch (_: ForthQuitNonInteractive) {
            // Caused by the EOF or \\\ commands --- stop reading this file, but
            // not an error --- will proceed to next file or to console
        } catch (_: ForthEOF) {
        } finally {
            vm.io = prevIO
            vm.verbosity = prevVerbosity
        }
    }

    /**  `include-primitives` `( in:"file" -- : Read class file of primitives )`
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc.
     * */

    fun w_includeBinary(vm: ForthVM) {
        val path = vm.getToken()
        readPrimitiveClass(vm, path)
    }
}