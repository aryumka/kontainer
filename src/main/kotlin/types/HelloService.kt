package types

class HelloService(private val bar: Bar) {
    fun hello() {
        println("Hello World")
    }

    fun getBar():Bar {
        return bar
    }

    fun getFoo():Foo {
        return bar.foo
    }
}