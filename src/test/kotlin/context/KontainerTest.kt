package context

import exception.CircularDependencyException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class Bar(val foo: Foo)

class Foo ()
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

class KontainerTest: FunSpec({

    context("Bean Registration") {
        test("should be able to register a bean") {
            val kontainer = Kontainer
            kontainer.register(Foo::class)
            kontainer.loadBeans()
            val bean = kontainer.getBean<Foo>("Foo")
            bean.shouldBeInstanceOf<Foo>()
        }
    }

    context("Dependency Injection") {
        test("should be able to inject dependencies") {
            val kontainer = Kontainer
            kontainer.register(Foo::class)
            kontainer.register(Bar::class)

            kontainer.loadBeans()

            val bar = kontainer.getBean<Bar>("Bar")
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

            val kontainer = Kontainer

            kontainer.register(A::class)
            kontainer.register(B::class)
            kontainer.register(C::class)
            kontainer.register(D::class)
            kontainer.register(E::class)
            kontainer.register(F::class)
            kontainer.register(G::class)

            kontainer.loadBeans()

            val a = kontainer.getBean<A>("A")
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
                val kontainer = Kontainer

                kontainer.register(W::class)
                kontainer.register(X::class)
                kontainer.register(Y::class)
                kontainer.register(Z::class)

                val exception = shouldThrow<CircularDependencyException> {
                    kontainer.loadBeans()
                }
            }
        }
    }
})