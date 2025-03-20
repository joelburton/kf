package kf.words.custom

import com.github.ajalt.mordant.terminal.Terminal
import kf.*

object wBload : IWordClass {
    override val name = "Include"
    override val description = "Include binary files"
    override val words get() = arrayOf(
        // including new primitives and forth files
        Word("BLOAD", ::w_includeBinary),
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

    /**  `include-primitives` `( in:"file" -- : Read class file of primitives )`
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc.
     * */

    fun w_includeBinary(vm: ForthVM) {
        val path = vm.interp.getToken()
        readPrimitiveClass(vm, path)
    }
}