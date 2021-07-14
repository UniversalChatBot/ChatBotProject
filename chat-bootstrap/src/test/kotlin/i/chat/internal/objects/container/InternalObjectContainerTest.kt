package i.chat.internal.objects.container

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InternalObjectContainerTest {
    private val empty: InternalObjectContainer
        get() = InternalObjectContainer()

    @Test
    fun put() {
        class A
        open class B
        class C : B()

        val m = empty
        val a = m.put("a", A())
        val b = m.put("b", B())
        val c = m.put(C(), arrayOf(B::class))
        assertEquals(a, m["a"])
        assertEquals(b, m["b"])
        assertEquals(c, m[B::class])
        assertEquals(c, m[C::class])
    }

    @Test
    fun putIfAbsent() {
        class A
        open class B
        class C : B()

        val m = empty
        assertTrue(m.putIfAbsent(A()))
        assertFalse(m.putIfAbsent(A()))
        assertTrue(m.putIfAbsent(C(), arrayOf(B::class)))
        assertFalse(m.putIfAbsent(B()))
        assertFalse(m.putIfAbsent(C()))
        assertTrue(m.putIfAbsent(m[B::class]))
        assertTrue(m.putIfAbsent(m[C::class]))
    }

    @Test
    fun remove() {
        class A
        open class B
        class C : B()

        val m = empty
        m.put(A())
        m.put(B())
        m.put(C())
        assertTrue(m.remove(A::class))
        assertFalse(m.remove(A::class))
        assertTrue(m.remove(m[B::class]))
        assertFalse(m.remove(B::class))

    }
    
}
