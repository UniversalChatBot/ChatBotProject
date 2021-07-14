package i.chat.dev.objects.manager

import kotlin.reflect.KClass

/**
 * 实例管理器
 *
 * 由此组件统一管理生命周期，
 * 根据规则自动创建、销毁类，
 *
 *
 */

abstract class IObjectsManager(initClass: KClass<*>) {

    /**
     * 替换子模块为自定义模块（仅可动态替换接口实现类）
     *
     */
    abstract fun <T : Any> override(implClass: KClass<in T>, superClass: KClass<T>)

    /**
     * 注意，在调用此方法前，仅会校验类与类之间的关联问题，并不会创建类的实例对象
     *
     * 根据规则启动,启动时将占用线程
     */
    abstract fun start()

    /**
     * 销毁此组件 (断开内部关联)
     */
    abstract fun destroy()

    /**
     * 根据键得到对应实例，如果未找到则抛出异常，
     * 外部类中，此方法仅可在
     * start() 方法之后
     * destroy() 之前执行
     *
     * @param key String
     * @return T
     */
    abstract fun <T : Any> get(key: String): T

    /**
     * 根据 class 对象得到合适的对象实例化
     */
    abstract fun <T : Any> get(key: KClass<out T>): T

    /**
     * 依据键添加外部实体，外部实体不受管控,
     *
     */
    abstract fun put(key: String, data: Any)

}
