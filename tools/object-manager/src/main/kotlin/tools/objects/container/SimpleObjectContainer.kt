package tools.objects.container

import tools.objects.create.ObjectManagerGenerate
import kotlin.reflect.KClass

/**
 * 一个简单的，基于构造函数和字段注入的类管理器
 *
 * @param T : Annotation 注解
 * @param replaceRule: (T) -> String 注解转换字段规则
 * @constructor
 */
class SimpleObjectContainer<T : Annotation>(
    annotationClass: KClass<T>,
    replaceRule: (T) -> String
) : BaseObjectContainer() {
    private val objectGenerate = ObjectManagerGenerate(objectManager = this, annotationClass, replaceRule)
    override fun <T : Any> generateObject(clazz: KClass<T>) = objectGenerate.generateObject(clazz)
}
