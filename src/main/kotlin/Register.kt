import kf.ForthVM
import kotlin.reflect.KProperty

/** Convenience for creating register: getters/setters with backing in mem. */

class RegisterDelegate(val addr: Int, val mem: IntArray) {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): Int {
        return mem[addr]
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: Int) {
        mem[addr] = value
    }
}
