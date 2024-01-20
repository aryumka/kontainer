package context

import types.Foo
import kotlin.reflect.KClass

class Container {
    private val registeredBean = mutableMapOf<String, Any>()

    fun register(name: String, kClass: KClass<*>) {
        registeredBean[name] =
    }

    fun createBean(kClass: KClass<*>): Any {
        val constructor = kClass.constructors.first()
        val parameters = constructor.parameters
        val args = parameters.map { parameter ->
            val bean = registeredBean[parameter.name]
            if (bean == null) {
                createBean(parameter.type.classifier as KClass<*>)
            } else {
                bean
            }
        }
        return constructor.call(*args.toTypedArray())
    }

    fun <T> getBean(name: String): T {
        return registeredBean[name] as T
    }
}
