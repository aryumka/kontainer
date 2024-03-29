package io.github.aryumka.context

import io.github.aryumka.exception.CircularDependencyException
import io.github.aryumka.exception.NoSuchBeanException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object Kontainer {
    private val createdBean = mutableMapOf<String, Any>()
    private val registeredBean = mutableListOf<KClass<*>>()
    private val dependencyGraph = mutableMapOf<KClass<*>, List<KClass<*>>>()
    private val edges = mutableListOf<Pair<KClass<*>, KClass<*>>>()
    private var rootIndices = intArrayOf()
    private var rootIdxMap = mutableMapOf<KClass<*>, Int>()

    //todo qualifier, KMP

    fun register(kClass: KClass<*>) {
        registeredBean.add(kClass)
        rootIdxMap[kClass] = registeredBean.size - 1
    }

    fun start() {
        setDependencyGraph()
        initRootIndices()
        unionFind()
        createBeans()
    }

    fun end() {
        createdBean.clear()
        registeredBean.clear()
        dependencyGraph.clear()
        edges.clear()
        rootIndices = intArrayOf()
        rootIdxMap.clear()
    }

    fun <T> getBean(name: String): T {
        if (!createdBean.containsKey(name) || createdBean[name] == null) {
            throw NoSuchBeanException("Bean $name not found")
        }
        return createdBean[name] as T
    }

    private fun setDependencyGraph() {
        for (bean in registeredBean) {
            val constructor = bean.primaryConstructor!!
            val parameters = constructor.parameters
            val parameterTypes = parameters.map { it.type.classifier as KClass<*> }
            dependencyGraph[bean] = parameterTypes

            for (parameter in parameterTypes) {
                edges.add(Pair(bean, parameter))
            }
        }
    }

    private fun initRootIndices() {
        rootIndices = IntArray(registeredBean.size)
        for (i in rootIndices.indices) rootIndices[i] = i
    }

    private fun unionFind() {
        for (edge in edges) {
            val (parent, child) = edge
            val parentIndex = rootIdxMap[parent]!!
            val childIndex = rootIdxMap[child]!!
            for (i in rootIndices.indices) {
                if (rootIndices[i] == childIndex) {
                    if (rootIndices[i] == rootIndices[parentIndex]) {
                        throw CircularDependencyException("Circular Dependency for $parent and $child")
                    }
                    rootIndices[i] = rootIndices[parentIndex]
                }
            }
        }
    }

    private fun createBeans() {
        for (i in rootIndices.indices) {
            if (rootIndices[i] == i) {
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