package kf.words.custom

import kf.*

object wBload : IWordModule {
    override val name = "kf.words.custom.wBload"
    override val description = "Include binary files"
    override val words get() = arrayOf(
        // including new primitives and forth files
        Word("BLOAD", ::w_bload),
        Word("BLOAD-AGAIN", ::w_bloadAgain),
    )

    /**  Read in a primitive class dynamically
     */
    fun readPrimitiveClass(vm: ForthVM,
                           name: String,
                           reloadOk: Boolean = false) {
        if (D) vm.dbg(3, "vm.readPrimitiveClass: $name")
        if (!reloadOk && name in vm.modulesLoaded) {
            vm.io.warning("Already loaded: $name (use BLOAD-AGAIN to reload)")
            return
        }
        val mod = try {
            Class.forName(name).kotlin.objectInstance!! as IWordModule
        } catch (e: ClassNotFoundException) {
            throw BloadError("Can't find: $name")
        } catch (e: NoSuchFieldException) {
            throw BloadError("Not an object")
        } catch (e: ClassCastException) {
            throw BloadError("Wrong interface: needs `name`, `words`")
        }
        vm.dict.addModule(mod)
    }

    /**  `BLOAD` `( in:"file" -- : Read class file of primitives )`
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc.
     * */

    fun w_bload(vm: ForthVM) {
        val path =  vm.source.scanner.parseName().strFromAddrLen(vm)
        readPrimitiveClass(vm, path, reloadOk=false)
    }

    /**  `BLOAD` `( in:"file" -- : Read class file of primitives )`
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc.
     * */

    fun w_bloadAgain(vm: ForthVM) {
        val path =  vm.source.scanner.parseName().strFromAddrLen(vm)
        readPrimitiveClass(vm, path, reloadOk=true)
    }
}