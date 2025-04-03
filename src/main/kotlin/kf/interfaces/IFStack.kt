package kf.interfaces

/** Stack in a memory location.
 *
 * This stack "grows downward" (ie, if sp=100, and then you push an item, sp
 * will be 99).
 */
interface IFStack {
    val name: String

    /** Location in memory of start of stack. */
    val startAt: Int

    /** Location in memory of end of stack-space */
    val endAt: Int

    /** Pointer to the current item. */
    var sp: Int

    /** Number of items in the stack. */
    val size: Int

    /**  Get stack as array (for debugging). */
    fun asArray(): IntArray

    /** Retrieves value from stack at a index relative to the stack origin.
     *
     * getAt(0) would be the first item ever added to the stack.
     * */
    fun getAt(n: Int): Int

    /** Retrieves value from stack at a index relative to the stack pointer.
     *
     * getFrom(0) is the same as peek()
     * */
    fun getFrom(n:Int): Int

    /** Remove & return value at index relative to stack pointer.
     *
     * Used to implement `ROLL`
     * */
    fun popFrom(n: Int): Int

    /** Add item. */
    fun push(n: Int)

    /** Add two items.
     *
     * Just an optimization, given how common this is. This reduces the number
     * of separate function calls and will perform better than the truly
     * variadic version,below.
     */
    fun push(a: Int, b: Int)

    /** Add any number of items. */
    fun push(vararg vs: Int)

    /** Push double num in two parts (first lo, then high)
     *
     * The project doesn't truly support double nums (they're not so urgent
     * when the normal cell size is already 32 bits). But there are lots of
     * API compatibility needed here. This takes a long, but only ever adds
     * the lower 32 bits and a 0 for the hi-cell.
     * */
    fun dblPush(n: Long)

    /** Pop double num in two parts (high first, then lo) */
    fun dblPop(): Long

    /** Remove & return top item. */
    fun pop(): Int

    /** Peek at top item. */
    fun peek(): Int

    /** String of simple dump of string (what `.s` uses) */
    fun simpleDumpStr(): String

    /** Dump a single line for the stack; this is used by `.S` */
    fun simpleDump()

    /**  Print a verbose stack dump. */
    fun dump()
}