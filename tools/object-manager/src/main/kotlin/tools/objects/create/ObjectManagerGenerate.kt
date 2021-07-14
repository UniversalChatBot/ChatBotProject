package tools.objects.create

import tools.objects.container.ObjectManager
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

class ObjectManagerGenerate<T : Annotation>(
    private val objectManager: ObjectManager,
    private val annotationClass: KClass<T>,
    private val replaceRule: (T) -> String

) : ConstructorObjectGenerate() {
    override fun <K : Any> generateObject(clazz: KClass<K>): K {
        val data = super.generateObject(clazz)
        // 触发字段注入
        invokeField(data)
        return data
    }

    private fun <K : Any> invokeField(data: K) {
        val clazz = data::class
        if (clazz.objectInstance != null &&
            clazz.isData
        ) {
            return
        }
        val field = clazz.memberProperties
            .filter { it is KMutableProperty<*> }
            .map { it as KMutableProperty<*> }
            .filter { it.annotations.map { its -> its::class }.contains(annotationClass).not() }
            .toList()
        for (property in field) {
            val annotation = property.annotations.first { it.annotationClass == annotationClass }
            val value = getDataFromAnnotation<K>(annotation, property.getter.returnType.jvmErasure)
            if (value == null && property.returnType.isMarkedNullable.not()) {
                throw RuntimeException("")
            }
            val setter = property.setter
            setter.isAccessible = true
            setter.call(data, value)
        }

    }

    override fun <K : Any> getConstructorObject(clazz: KClass<K>, parameter: KParameter): K? {
        val annotations = parameter.annotations.filter { it::class != annotationClass }.toList()
        return if (annotations.isNotEmpty()) {
            getDataFromAnnotation(annotations.first(), parameter.type.jvmErasure)
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <K : Any> getDataFromAnnotation(annotation: Annotation, clazz: KClass<*>): K? {
        val key = replaceRule(annotation as T)
        return if (objectManager.containsKey(key).not()) {
            if (objectManager.containsKey(clazz).not()) {
                null
            } else {
                objectManager.getOrElse(clazz) {
                    throw RuntimeException("发生线程安全问题")
                } as K
            }
        } else {
            objectManager.getOrElse<K>(key) {
                throw RuntimeException("发生线程安全问题")
            }
        }
    }

}
