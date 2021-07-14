package tools.objects.container

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass


internal class SimpleObjectContainerTest {
    annotation class Param(val name: String = "")

    private fun getSimpleManager(vararg data: Pair<String, Any>): SimpleObjectContainer<Param> {
        val simpleObjectManager = SimpleObjectContainer(Param::class) { t -> t.name }
        data.forEach {
            if (it.first.isEmpty()) {
                if (it.second is KClass<*>) {
                    simpleObjectManager.put(it.second as KClass<*>)
                } else {
                    simpleObjectManager.put(it.second)
                }
            } else {
                if (it.second is KClass<*>) {
                    simpleObjectManager.put(it.first, it.second as KClass<*>)
                } else {
                    simpleObjectManager.put(it.first, it.second)
                }
            }
        }
        return simpleObjectManager
    }

    @Test
    fun put() {
        class Test1

        val s = getSimpleManager()
        val put = s.put(Test1::class)
        val put1 = s.put(Test1::class)
        assertNotEquals(put.hashCode(), put1.hashCode())
        class Test2(@Param val test1: Test1)

        val put2 = s.put(Test2::class)
        assertEquals(put2.test1.hashCode(), put1.hashCode())
        s.put("name", "dragon")
        class Test3(val data: String = "defaultValue")
        class Test4(@Param("name") val data: String = "defaultValue")
        assertEquals("defaultValue", s.put(Test3::class).data)
        assertEquals("dragon", s.put(Test4::class).data)
    }

    @Test
    fun putIfAbsent() {
        class Test1
        class Test2
        class Test3
        class Test4

        val s = getSimpleManager(Pair("", Test1::class), Pair("", Test2::class))
        val get = s.get<Test1>()
        assertFalse(s.putIfAbsent(Test1::class))
        val get1 = s.get<Test1>()
        assertEquals(get.hashCode(), get1.hashCode())
        assertTrue(s.putIfAbsent("class3", Test3::class))
        assertFalse(s.putIfAbsent("class3", Test3::class))
        val test4 = Test4()
        assertTrue(s.putIfAbsent(test4))
        assertTrue(s.putIfAbsent(test4))
    }

    @Test
    fun remove() {
        class Test1
        class Test2

        val m = getSimpleManager(Pair("testA", Test1::class), Pair("testB", Test2::class))
        assertTrue(m.remove("testA"))
        assertFalse(m.remove("testA"))
        val news = m.get<Test2>("testB")
        m.put("testC", news)
        m.put("testD", news)
        m.remove(news)
        assertFalse(m.containsKey("testB"))
        assertFalse(m.containsKey("testC"))
        assertFalse(m.containsKey("testD"))

    }

    @Test
    fun clear() {
        class Test1
        class Test2

        val m = getSimpleManager(Pair("testA", Test1::class), Pair("testB", Test2::class))
        assertFalse(m.length == 0)
        m.clear()
        assertTrue(m.length == 0)

    }


    @Test
    fun getOrElse() {
        class Test1
        class Test2

        val m = getSimpleManager(Pair("testA", Test1::class), Pair("", Test2::class))
        m.getOrElse<Test1>("testA") { throw RuntimeException("") }
        m.getOrElse(Test2::class) { throw RuntimeException("") }
        assertThrows<RuntimeException>("not found") {
            m.getOrElse(Test1::class) { throw RuntimeException("not found") }
        }
        assertThrows<RuntimeException>("not found") {
            m.getOrElse<Test2>("testB") { throw RuntimeException("not found") }
        }

    }


    @Test
    fun containsKey() {
        class Test1
        class Test2

        val m = getSimpleManager(Pair("testA", Test1::class), Pair("", Test2::class))
        assertTrue(m.containsKey("testA"))
        assertTrue(m.containsKey(Test2::class))
        assertFalse(m.containsKey(Test1::class))
        assertFalse(m.containsKey("testB"))
    }

    @Test
    fun getValue() {
        class Test1
        class Test2

        val m = getSimpleManager(Pair("testA", Test1::class), Pair("", Test2::class))
        val a: Test1 = m["testA"]
        val b: Test2 = m[Test2::class]
        assertNotNull(a)
        assertNotNull(b)
    }

    @Test
    fun from() {
        class Test1
        class Test2

        val m = getSimpleManager(Pair("testA", Test1::class), Pair("", Test2::class))
        val test2a1: Test2 by m.from()
        val test2a2 by m.from(Test2::class)
        val test1:Test1 by m.from("testA")
        assertNotNull(test2a1)
        assertNotNull(test2a2)
        assertNotNull(test1)
    }

}


