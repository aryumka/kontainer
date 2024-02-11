# Kontainer

A simple DI container written in Kotlin.
- register beans
- validate bean dependencies
- resolve bean instances

## Usage

```kotlin
//register a service
fun main() {
    val kontainer = Kontainer()
    //register a bean
    kontainer.register(HelloService::class)
    //start the kontainer
    kontainer.start()
    //resolve an instance
    val helloService = kontainer.getBean<HelloService>("HelloService")
}
```