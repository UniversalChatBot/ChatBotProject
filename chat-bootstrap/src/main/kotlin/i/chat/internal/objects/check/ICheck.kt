package i.chat.internal.objects.check

/**
 * 测试方法：用于检测
 */
interface ICheck<T : Any> {

    fun check(
        clazz: T, fail: (Throwable) -> Boolean = {
            false
        }
    ): Boolean = try {
        checkInternal(clazz)
    } catch (e: Throwable) {
        fail(e)
    }

    fun checkInternal(clazz: T): Boolean

    class ObjectCheckFailException(msg: String) : RuntimeException(msg)

    fun objExp(msg: String) = ObjectCheckFailException(msg)
}
