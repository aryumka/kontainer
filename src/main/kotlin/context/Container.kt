package context

class Container {
    private val beans = mutableMapOf<String, Any>()

    fun <T : Any> register(name: String, bean: T) {
        beans[name] = bean
    }

    fun <T> getBean(name: String): T {
        return beans[name] as T
    }
}
