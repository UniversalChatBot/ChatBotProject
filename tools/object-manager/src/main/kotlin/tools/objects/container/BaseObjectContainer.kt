package tools.objects.container

import tools.objects.create.ObjectGenerate
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

abstract class BaseObjectContainer : MutableObjectContainer, ObjectGenerate {
    private val objects = ConcurrentHashMap<String, Any>()

    override fun <T : Any> put(key: String, clazz: KClass<T>) = lock { put(key, generateObject(clazz)) }

    override fun <T : Any> put(key: String, data: T) = lock {
        objects[genStringKey(key)] = data
        data
    }

    override fun <T : Any> put(data: T, superClass: Array<KClass<in T>>) = lock {
        val name = data::class.javaObjectType.name
        if (name.startsWith("java.lang")) {
            throw RuntimeException("请不要将基本数据类型[${name}]作为唯一字段添加！")
        }
        superClass.filter { it != data::class }.forEach {
            objects[genKClassKey(it)] = data
        }
        objects[genKClassKey(data::class)] = data
        data
    }


    override fun <T : Any> put(clazz: KClass<T>, superClass: Array<KClass<in T>>): T {
        synchronized(objects) {
            return put(generateObject(clazz), superClass)
        }
    }

    override fun <T : Any> putIfAbsent(key: String, clazz: KClass<T>): Boolean {
        synchronized(objects) {
            if (containsKey(key).not()) {
                put(key, clazz)
                return true
            }
        }
        return false
    }

    override fun <T : Any> putIfAbsent(key: String, data: T): Boolean {
        synchronized(objects) {
            if (objects.containsKey(key).not()) {
                put(key, data)
                return true
            }
        }
        return false
    }

    override fun <T : Any> putIfAbsent(clazz: KClass<T>, superClass: Array<KClass<in T>>) =
        lock {
            if (containsKey(clazz).not()) {
                put(clazz, superClass)
                true
            } else {
                false
            }
        }


    override fun <T : Any> putIfAbsent(data: T, superClass: Array<KClass<in T>>) = lock {
        if (containsKey(data::class).not() && superClass.none { containsKey(it) }) {
            put(data, superClass)
            true
        } else {
            get(data::class) == data
        }
    }

    override fun remove(key: String) = lock {
        if (containsKey(key)) {
            objects.remove(genStringKey(key))
            true
        } else {
            false
        }
    }

    override fun remove(key: KClass<*>) = lock {
        if (containsKey(key)) {
            objects.remove(genKClassKey(key))
            true
        } else {
            false
        }
    }

    override fun remove(obj: Any) = lock {
        var nothing = false
        objects.filter { it.value == obj }.map { it.key }.forEach {
            nothing = true
            objects.remove(it)
        }
        nothing
    }

    override fun clear() = lock {
        objects.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T : Any> get(clazz: KClass<T>): T {
        return (objects[genKClassKey(clazz)]
            ?: throw NullPointerException("class [${clazz.simpleName}] not found！ ")) as T
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T : Any> get(key: String): T {
        return (objects[genStringKey(key)] ?: throw NullPointerException("key [$key] not found！ ")) as T

    }

    override fun <T : Any> getOrElse(clazz: KClass<T>, elseFun: () -> T) = lock {
        if (containsKey(clazz)) {
            get(clazz)
        } else {
            elseFun()
        }
    }

    override fun <T : Any> getOrElse(key: String, elseFun: () -> T): T {
        return if (containsKey(key)) {
            get(key)
        } else {
            elseFun()
        }
    }

    override fun containsKey(clazz: KClass<*>): Boolean {
        return objects.containsKey(genKClassKey(clazz))
    }

    override fun containsKey(key: String): Boolean {
        return objects.containsKey(genStringKey(key))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(thisRef: Any?, property: KProperty<*>): T {
        return get(property.returnType.jvmErasure) as T
    }

    inner class WithClassReadOnlyProperty<T : Any>(private val clazz: KClass<out T>) : ReadOnlyProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return get(clazz)
        }
    }

    override fun <T : Any> from(clazz: KClass<T>): ReadOnlyProperty<Any?, T> {
        return WithClassReadOnlyProperty(clazz)
    }

    inner class WithKeyReadOnlyProperty<T : Any>(private val key: String) : ReadOnlyProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return get(key)
        }
    }

    override fun <T : Any> from(key: String): ReadOnlyProperty<Any?, T> {
        return WithKeyReadOnlyProperty(key)
    }

    override val length: Int
        get() = objects.size

    private fun genStringKey(strKey: String): String {
        return "str:$strKey"
    }

    private fun genKClassKey(classKey: KClass<*>): String {
        return "cls:${classKey.javaObjectType.name}"
    }

    private fun <T : Any> lock(func: MutableObjectContainer.() -> T): T {
        return synchronized(objects) {
            func(this)
        }
    }
}
