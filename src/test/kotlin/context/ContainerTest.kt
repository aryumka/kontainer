package context

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import types.Bar
import types.Foo


class ContainerTest: FunSpec({

    context("Bean Registration") {
        test("should be able to register a bean") {
            val container = Container()
            val foo = Foo()
            container.register("Foo", Foo::class)
            val bean = container.getBean<Foo>("Foo")
            bean::class shouldBe foo::class
        }
    }

    context("Dependency Injection") {
        test("should be able to inject dependencies") {
            val container = Container()
            container.register("Foo", Foo::class)
            container.register("Bar", Bar::class)
            getBean<Bar>("Bar").foo::class shouldBe Foo::class
        }
    }

})