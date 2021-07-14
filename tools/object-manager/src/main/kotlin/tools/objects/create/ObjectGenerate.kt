package tools.objects.create

import kotlin.reflect.KClass

interface ObjectGenerate {
     fun <T : Any> generateObject(clazz: KClass<T>): T
}
