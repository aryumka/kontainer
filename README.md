# Kontainer
A simple DI container written in Kotlin available in maven central repository.

## Why I implemented `Kontainer` myself?
I wanted to understand how DI containers work under the hood. I also wanted to understand how to implement a simple DI container in Kotlin.

## Features
- Bean registration
- Dependency resolution
- Resolving beans by name and type
- Circular dependency detection

## Setup
Add the plugin to your `build.gradle.kts` file.
```kotlin
plugins {
    implementation("io.github.aryumka:kontainer:0.0.1")
    implementation(kotlin("reflect"))
}
```

## Usage

```kotlin
class HelloDao {
    fun save(message: String) {
        println("Saving message: $message")
    }
}

class HelloService(dao: HelloDao) {
    fun sayHello() {
        dao.save("Hello, World!")
    }
}

//register a service
fun main() {
    val kontainer = Kontainer()
    //register beans 
    kontainer.register(HelloService::class)
    kontainer.register(HelloDao::class)
    
    //start the kontainer
    kontainer.start()
    
    //resolve an instance
    val helloService = kontainer.getBean<HelloService>("HelloService")
    helloService.sayHello() //prints "Saving message: Hello, World!"
}
```

**Note: This is inspired by DI containers such as Spring and Koin
and is created for educational purposes. It may not cover all edge cases and
optimizations present in production-ready libraries.

## License
MIT