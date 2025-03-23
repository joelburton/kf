package words.core.ext

import EvalForthTestCase
import ForthTestCase
import dummyFn
import kf.ForthDeferredWordError
import kf.Word
import kf.words.core.ext.wDeferExt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class wDeferExtTest : ForthTestCase() {
    val mod = wDeferExt

    init {
        vm.dict.addModule(wDeferExt)
    }

    @Test
    fun w_defer() {
        vm.scanner.fill("new-word")
        mod.w_defer(vm)
        assertEquals("new-word", vm.dict.last.name)
    }

    @Test
    fun w_is() {
        vm.scanner.fill("new-word")
        mod.w_defer(vm)
        val deferW = vm.dict.last

        val srcW = Word(
            "random-word",
            ::dummyFn,
            cpos = 10,
            dpos = 20,
        )
        vm.dict.add(srcW)

        vm.scanner.fill("new-word")
        vm.dstk.push(srcW.wn)
        mod.w_is(vm)

        assertEquals(srcW.cpos, deferW.cpos)
        assertEquals(srcW.dpos, deferW.dpos)
        assertEquals(srcW.fn, deferW.fn)
        assertEquals(srcW.wn, deferW.deferToWn)
    }

    @Test
    fun w_deferStore() {
        vm.scanner.fill("new-word")
        mod.w_defer(vm)
        val deferW = vm.dict.last

        val srcW = Word(
            "random-word",
            ::dummyFn,
            cpos = 10,
            dpos = 20,
        )
        vm.dict.add(srcW)

        vm.dstk.push(srcW.wn, deferW.wn)
        mod.w_deferStore(vm)

        assertEquals(srcW.cpos, deferW.cpos)
        assertEquals(srcW.dpos, deferW.dpos)
        assertEquals(srcW.fn, deferW.fn)
        assertEquals(srcW.wn, deferW.deferToWn)
    }

    @Test
    fun w_deferFetch() {
        vm.scanner.fill("new-word")
        mod.w_defer(vm)
        val deferW = vm.dict.last

        val srcW = Word(
            "random-word",
            ::dummyFn,
            cpos = 10,
            dpos = 20,
        )
        vm.dict.add(srcW)
        vm.dstk.push(srcW.wn, deferW.wn)
        mod.w_deferStore(vm)

        vm.dstk.push(deferW.wn)
        mod.w_deferFetch(vm)
        assertDStack(srcW.wn)
    }

    @Test
    fun w_actionOf() {
        vm.scanner.fill("new-word")
        mod.w_defer(vm)
        val deferW = vm.dict.last

        val srcW = Word(
            "random-word",
            ::dummyFn,
            cpos = 10,
            dpos = 20,
        )
        vm.dict.add(srcW)
        vm.dstk.push(srcW.wn, deferW.wn)
        mod.w_deferStore(vm)

        vm.scanner.fill("new-word")
        mod.w_actionOf(vm)
        assertDStack(srcW.wn)
    }
}

class wDeferExtFuncTest : EvalForthTestCase() {
    @Test
    fun defer() {
        eval("defer new-word")

        assertFailsWith<ForthDeferredWordError> {
            eval("new-word")
        }

        val deferW = vm.dict["new-word"]
        val dup = vm.dict["dup"]
        val drop = vm.dict["drop"]

        eval("${dup.wn} is new-word")
        eval("42 new-word")
        assertDStackKeep(42, 42)

        eval("${drop.wn} ${deferW.wn} defer!")
        eval("new-word")
        assertDStack(42)

        eval("${deferW.wn} defer@")
        assertDStack(drop.wn)

        eval("action-of new-word")
        assertDStack(drop.wn)
    }
}