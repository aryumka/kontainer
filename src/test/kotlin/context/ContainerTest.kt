package context

import Exception.CircularDependencyException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import types.Bar
import types.Foo


class ContainerTest: FunSpec({

    context("Bean Registration") {
        test("should be able to register a bean") {
            val container = Container()
            val foo = Foo()
            container.register(Foo::class)
            container.loadBeans()
            val bean = container.getBean<Foo>("Foo")
            bean::class shouldBe foo::class
        }
    }

    context("Dependency Injection") {
        test("should be able to inject dependencies") {
            val container = Container()
            container.register(Foo::class)
            container.register(Bar::class)

            container.loadBeans()

            val bar = container.getBean<Bar>("Bar")
            bar.foo::class shouldBe Foo::class
        }

        test("should be able to inject dependencies with multi level dependencies") {
            class E
            class B
            class C
            class D(val e: E)
            class A(
                val b: B,
                val c: C,
                val d: D
            )
            class G
            class F(val g: G)

            val container = Container()

            container.register(A::class)
            container.register(B::class)
            container.register(C::class)
            container.register(D::class)
            container.register(E::class)
            container.register(F::class)
            container.register(G::class)

            container.loadBeans()

            val a = container.getBean<A>("A")
            a.b::class shouldBe B::class
            a.c::class shouldBe C::class
            a.d::class shouldBe D::class
            a.d.e::class shouldBe E::class
        }
    }
})

class W
class X(private val y: Y)
class Z(private val w: W, private val x: X)
class Y(private val z: Z)

class ExceptionTest: FunSpec({
    context("Exception handling") {
        context("Circular Dependency") {
            test("should throw exception when circular dependency is detected") {
                val container = Container()

                container.register(W::class)
                container.register(X::class)
                container.register(Y::class)
                container.register(Z::class)

                val exception = shouldThrow<CircularDependencyException> {
                    container.loadBeans()
                }
            }
        }
    }
})