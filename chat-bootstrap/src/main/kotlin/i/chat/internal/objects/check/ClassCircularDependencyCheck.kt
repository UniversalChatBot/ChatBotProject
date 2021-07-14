package i.chat.internal.objects.check

import i.chat.dev.objects.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * 类的循环依赖测试
 */
class ClassCircularDependencyCheck : ICheck<KClass<*>> {
    /**
     * 类的信息
     */
    data class ClassInfo<T : Any>(
        val clazz: KClass<T>,
        val name: String,
        val dependencies: MutableSet<KClass<*>> = mutableSetOf(),
    )


    override fun checkInternal(clazz: KClass<*>): Boolean {
        TODO()
    }

    private fun <T : Any> loadDependency(sets: MutableSet<ClassInfo<*>>, clazz: KClass<T>) {
        if (clazz.hasAnnotation<Component>()) {
            val ann = clazz.findAnnotation<Component>()
            val constructor =
                clazz.primaryConstructor ?: throw objExp("class '${clazz.qualifiedName}' not primary constructor.")
            val name = if (ann!!.name == "") clazz.java.name else ann.name
            if (constructor.parameters.isEmpty()) {
                sets.add(ClassInfo(clazz, name))
            } else {
                constructor.parameters
                    .filter { it.type.jvmErasure.hasAnnotation<Component>() }
            }
        } else {
            return
        }
    }

    private fun checkCircularDependency(clazz: KClass<*>) {

    }


}
