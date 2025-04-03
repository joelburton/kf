package kf.words.custom

import kf.BloadError
import kf.D
import kf.ForthVM
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.strFromAddrLen

object wBload : IWordModule {
    override val name = "kf.words.custom.wBload"
    override val description = "Include binary files"
    override val words get(): Array<Word> = arrayOf(
        // including new primitives and forth files
        Word("BLOAD", ::w_bload),
        Word("BLOAD-AGAIN", ::w_bloadAgain),
    )

    /**  Read in a primitive class dynamically
     */
    fun readPrimitiveClass(inVm: IForthVM,
                           name: String,
                           reloadOk: Boolean = false) {
        val vm = inVm as ForthVM
        if (D) vm.dbg(3, "vm.readPrimitiveClass: $name")
        if (!reloadOk && name in vm.modulesLoaded) {
            vm.io.warning("Already loaded: $name (use BLOAD-AGAIN to reload)")
            return
        }
        val mod = try {
            Class.forName(name).kotlin.objectInstance!! as IWordModule
        } catch (_: ClassNotFoundException) {
            throw BloadError("Can't find: $name")
        } catch (_: NoSuchFieldException) {
            throw BloadError("Not an object")
        } catch (_: ClassCastException) {
            throw BloadError("Wrong interface: needs `name`, `words`")
        }
        vm.dict.addModule(mod)
    }

    /**  `BLOAD` `( in:"file" -- : Read class file of primitives )`
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc.
     * */

    fun w_bload(vm: IForthVM) {
        val path =  vm.source.scanner.parseName().strFromAddrLen(vm)
        readPrimitiveClass(vm, path, reloadOk=false)
    }

    /**  `BLOAD` `( in:"file" -- : Read class file of primitives )`
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc.
     * */

    fun w_bloadAgain(vm: IForthVM) {
        val path =  vm.source.scanner.parseName().strFromAddrLen(vm)
        readPrimitiveClass(vm, path, reloadOk=true)
    }
}