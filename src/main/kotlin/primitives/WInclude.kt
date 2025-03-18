package kf.primitives

import com.github.ajalt.mordant.terminal.Terminal
import kf.*

object WInclude : WordClass {
    override val name = "Include"
    override val primitives get() = arrayOf(
        // including new primitives and forth files
        Word("include", ::w_include),
        Word("include-primitives", ::w_includeBinary),
    )

    /**  Read in a primitive class dynamically
     */
    fun readPrimitiveClass(vm: ForthVM, name: String) {
        if (D) vm.dbg(3, "vm.readPrimitiveClass: $name")
        try {
            // get the actual "forth module" (ie, Kotlin object) -- Kotlin
            // objects are a type of singleton Java class, so there's a field
            // always called INSTANCE on the class with the object
            val mod = Class.forName(name)
                .getDeclaredField("INSTANCE")
                .get(null) as IWordClass
            vm.dict.addModule(mod)
        } catch (e: ClassNotFoundException) {
            throw ForthError("Can't find: $name")
        } catch (e: NoSuchFieldException) {
            throw ForthError("Not an object")
        } catch (e: ClassCastException) {
            throw ForthError("Wrong interface: needs `name`, `primitives`")
        }
    }


    // *************************************************************************

    /**  `include` `( in:"file" -- : read Forth file in )` */

    fun w_include(vm: ForthVM) {
        val path = vm.interp.getToken()

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
        val path = vm.interp.getToken()
        readPrimitiveClass(vm, path)
    }
}