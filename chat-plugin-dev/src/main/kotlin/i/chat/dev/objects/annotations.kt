package i.chat.dev.objects

import kotlin.reflect.KClass


/**
 * 标记此为一个自动注入类
 *
 * @param name 创建一个名称
 */
annotation class Component(
    val name: String = ""
)

/**
 *字段标记，为类中field字段自动注入
 *
 * @property name String
 * @constructor
 */
annotation class Resource(
    val name: String = ""
)

/**
 * 构造函数参数字段注入
 * @property name String 自定义名称
 * @constructor
 */
annotation class Param(
    val name: String = ""
)


/**
 * 接口实现类标记
 *
 * @property name String 自定义名称
 * @constructor
 */
@Component
annotation class Service(
    val name: String = ""
)


/**
 *
 * 为Service 强制标记子类，仅在无自动扫描时使用，此接口可不加
 *
 * @property impl Array<KClass<*>>
 * @constructor
 */
annotation class ServiceInterface(
    val impl: Array<KClass<*>> = []
)
