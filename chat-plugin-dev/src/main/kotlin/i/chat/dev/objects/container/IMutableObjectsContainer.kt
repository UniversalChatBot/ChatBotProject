package i.chat.dev.objects.container

import kotlin.reflect.KClass

interface IMutableObjectsContainer :IObjectsContainer{

    /**
     * 添加一个绑定到键的对象
     */
    fun <T : Any> put(
        key: String,
        data: T
    ): T

    /**
     * 添加一个绑定到 CLASS 的对象
     *
     * @param superClass Array<KClass<in T>> 链接父类对象
     */
    fun <T : Any> put(
        data: T,
        superClass: Array<KClass<in T>> = arrayOf(),
    ): T

    /**
     * 如果不存在相同键，则添加一个绑定到键的对象
     *
     * @return Boolean 返回是否创建新类
     */

    fun <T : Any> putIfAbsent(
        key: String,
        data: T
    ): Boolean

    /**
     * 如果不存在相同的类，则添加绑定到 CLASS 的对象
     */
    fun <T : Any> putIfAbsent(
        data: T,
        superClass: Array<KClass<in T>> = arrayOf()
    ): Boolean

    /**
     * 根据键移除实例
     *
     */
    fun remove(key: String): Boolean

    /**
     * 根据kClass 移除绑定到此 class 的对象
     */
    fun remove(key: KClass<*>): Boolean

    /**
     *
     * 在此 objectManager 下取消所有与 obj 关联的连接
     *
     */
    fun remove(obj: Any): Boolean

    /**
     * 移除全部对象
     */
    fun clear()
}
