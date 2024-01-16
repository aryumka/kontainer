package context

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MoneyTests: FunSpec({
    class Foo(val value: String)

    context("Registering a bean") {
        test("should be able to register a bean") {
            val container = Container()
            val foo = Foo("bar")
            container.register("foo", foo)
            container.getBean<Foo>("foo") shouldBe foo
        }
    }

})