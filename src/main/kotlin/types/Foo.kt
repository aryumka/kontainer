package types

import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

class Foo private constructor(private val name: String = "Foo"){
    override fun toString(): String {
        return "Foo(name='$name')"
    }
}