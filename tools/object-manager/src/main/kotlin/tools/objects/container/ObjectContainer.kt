package tools.objects.container

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * 类和对象关系管理器
 *
 *
 */
interface ObjectManager {

    /**
     *  根据获取CLASS得到对象
     *
     * 注意：
     * - 当对象不存在时将会抛出 NULL 错误
     * - 当类型不匹配时则会抛出 CAST 错误
     *
     * @param clazz KClass<T> class 对象
     * @return T 实例化对象
     */
    operator fun <T : Any> get(clazz: KClass<T>): T

    /**
     * 根据键名称得到对象
     *
     * 注意：
     * - 当对象不存在时将会抛出 NULL 错误
     * - 当类型不匹配时则会抛出 CAST 错误
     *
     * @param key String 键名称
     * @return T 实例化对象
     */
    operator fun <T : Any> get(key: String): T

    /**
     * 获取字段数值，如果不存在则执行else 方法
     */
    fun <T : Any> getOrElse(key: String, elseFun: () -> T): T

    /**
     * 获取字段数值，如果不存在则执行else 方法
     */
    fun <T : Any> getOrElse(clazz: KClass<T>, elseFun: () -> T): T

    /**
     * 判断是否存在与查找 class 对象匹配的实例
     *
     * @param clazz KClass<*> class 对象
     * @return Boolean 结果
     */
    fun containsKey(clazz: KClass<*>): Boolean

    /**
     *
     * 判断是否存在与查找键匹配的实例
     *
     * @param key String 键
     * @return Boolean 结果
     */
    fun containsKey(key: String): Boolean

    /**
     * **Kotlin Function:** by
     *
     *  根据接收的 Class 对象来获取实例
     *
     */
    operator fun <T : Any> getValue(thisRef: Any?, property: KProperty<*>): T

    /**
     * @see ObjectManager.get(KClass)
     *
     */
    fun <T : Any> from(clazz: KClass<T>): ReadOnlyProperty<Any?, T>

    /**
     * @see ObjectManager.get(String)
     */
    fun <T : Any> from(key: String): ReadOnlyProperty<Any?, T>


    /**
     * 绑定的实例数量
     */
    val length: Int

}

inline fun <reified T : Any> ObjectManager.from(): ReadOnlyProperty<Any?, T> {
    return from(T::class)
}

inline fun <reified T : Any> ObjectManager.get(): T {
    return get(T::class)
}
