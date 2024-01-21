package context

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Container {
    val createdBean = mutableMapOf<String, Any>()
    private val registeredBean = mutableListOf<KClass<*>>()
    private val dependencyGraph = mutableMapOf<KClass<*>, List<KClass<*>>>()
    private val edgeList = mutableListOf<Pair<KClass<*>, KClass<*>>>()
    private var rootList = intArrayOf()
    private var rootMap = mutableMapOf<KClass<*>, Int>()

    fun register(kClass: KClass<*>) {
        registeredBean.add(kClass)
        rootMap[kClass] = registeredBean.size - 1
    }

    fun loadBeans() {
        makeGraph()
        makeRootList()
        unionFind()
        createBeans()
    }

    inline fun <reified T> getBean(name: String): T {
        return createdBean[name] as T
    }

    private fun makeGraph() {
        for (bean in registeredBean) {
            val constructor = bean.primaryConstructor!!
            val parameters = constructor.parameters
            val parameterTypes = parameters.map { it.type.classifier as KClass<*> }
            dependencyGraph[bean] = parameterTypes

            for (parameter in parameterTypes) {
                edgeList.add(Pair(bean, parameter))
            }
        }
    }

    private fun makeRootList() {
        rootList = IntArray(registeredBean.size)
        for (i in rootList.indices) rootList[i] = i
    }

    private fun unionFind() {
        for (edge in edgeList) {
            val (parent, child) = edge
            val parentIndex = rootMap[parent]!!
            val childIndex = rootMap[child]!!
            for (i in rootList.indices) {
                if (rootList[i] == childIndex) {
                    if (rootList[i] == rootList[parentIndex]) {
                        throw Exception("Circular Dependency for $parent and $child")
                    }
                    rootList[i] = rootList[parentIndex]
                }
            }
        }
    }

    private fun createBeans() {
        for (i in rootList.indices) {
            if (rootList[i] == i) {
                createBean(registeredBean[i], dependencyGraph[registeredBean[i]]!!)
            }
        }
    }

    private fun createBean(k: KClass<*>, v:List<KClass<*>>) {
        for (i in v.indices) {
            if (!createdBean.containsKey(v[i].simpleName!!) || createdBean[v[i].simpleName!!] == null) {
                createBean(v[i], dependencyGraph[v[i]]!!)
            }
        }
        createdBean[k.simpleName!!] = k.primaryConstructor!!.call(*v.map { createdBean[it.simpleName!!] }.toTypedArray())
    }
}

