import context.Container
import types.Bar
import types.Foo
import types.HelloService

fun main() {
    val container = Container
    container.register(HelloService::class)
    container.register(Foo::class)
    container.register(Bar::class)
    container.loadBeans()
    val helloWorld = container.getBean<HelloService>("HelloService")
    helloWorld.hello()
}

fun ddd(name: String){TODO()}