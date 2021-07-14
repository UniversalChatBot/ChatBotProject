package i.chat.internal.objects.manager

import i.chat.dev.objects.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDependencies

/**
 * 针对类与类之间的关系进行校验
 */
class InternalObjectCorrelationCheck {
    /**
     * 校验循
     */
    fun checkAll(initClass: KClass<*>) {
        if (initClass.hasAnnotation<Component>().not() || initClass.isAbstract) {
            throw objExp("[${initClass.qualifiedName}] 未标记为注入类.")
        }
        val constr = initClass.primaryConstructor ?: throw objExp("未找到[${initClass.qualifiedName}]下可用的构造函数.")
        constr.parameters.forEach { it ->

        }
    }



    class ObjectCheckFailException(msg: String) : RuntimeException(msg)

    private fun objExp(msg: String) = ObjectCheckFailException(msg)
}
