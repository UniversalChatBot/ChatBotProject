package i.chat.internal.objects.manager

import i.chat.dev.objects.container.IMutableObjectsContainer
import i.chat.dev.objects.manager.IObjectsManager
import i.chat.internal.objects.container.InternalObjectContainer
import kotlin.reflect.KClass

/**
 * 组件类管理器
 *
 */
class InternalObjectsManager(
    private val initClass: KClass<*>,
    private val objectsContainer: IMutableObjectsContainer = InternalObjectContainer()
) : IObjectsManager(initClass) {




    init {
        // 校验循环依赖
    }


    @Synchronized
    override fun <T : Any> override(implClass: KClass<in T>, superClass: KClass<T>) {

    }

    @Synchronized
    override fun start() {

    }

    @Synchronized
    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun <T : Any> get(key: String): T {
        TODO("Not yet implemented")

    }

    override fun <T : Any> get(key: KClass<out T>): T {
        TODO("Not yet implemented")

    }

    @Synchronized
    override fun put(key: String, data: Any) {
    }
}
