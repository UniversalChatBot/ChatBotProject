package tools.objects.create

import sun.misc.Unsafe
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

abstract class ConstructorObjectGenerate : ObjectGenerate {
    override fun <T : Any> generateObject(clazz: KClass<T>): T {
        // 针对 kotlin object
        if (clazz.objectInstance != null) {
            return clazz.objectInstance!!
        }
        if (clazz.isData) {
            // plugin.no-arg
            val toList = clazz.constructors.filter { it.parameters.isEmpty() }.toList()
            if (toList.isNotEmpty()) {
                return toList.first().call()
            } else {
                // warn : used unsupported function
                val unsafe = Unsafe.getUnsafe()
                @Suppress("UNCHECKED_CAST")
                return unsafe.allocateInstance(clazz.javaObjectType) as T
            }
        }
        val primaryConstructor = clazz.primaryConstructor ?: throw RuntimeException("未找到${clazz.qualifiedName}可用的构造函数。")
        val parameters = primaryConstructor.parameters
        if (parameters.isEmpty()) {
            return primaryConstructor.call()
        }
        val call: MutableMap<KParameter, Any?> = LinkedHashMap()
        for (parameter in parameters) {
            val data = getConstructorObject(clazz, parameter)
            if (data != null) {
                val paramClass = parameter.type.jvmErasure
                if (data::class.isSuperclassOf(paramClass)) {
                    call[parameter] = data
                } else {
                    throw RuntimeException(
                        "Rule Bug:注入对象（${data::class.qualifiedName}）非" +
                                "被注入字段${paramClass.qualifiedName}相关类."
                    )
                }
            } else {
                if (parameter.isOptional) {
                    // 无注解，且字段存在默认值，跳过注入
                    continue
                } else {
                    throw RuntimeException("未找到字段 $parameter 的可用数据.")
                }
            }
        }
        return primaryConstructor.callBy(call)
    }

    /**
     * 根据class 和 字段得到对象
     *
     * @param clazz KClass<T>
     * @param parameter KParameter
     * @return T?
     */
    abstract fun <T : Any> getConstructorObject(clazz: KClass<T>, parameter: KParameter): T?

}

