import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.0.0"
}

group = "io.github.aryumka"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

tasks.test {
    useJUnitPlatform()
}

centralPortal {
    pom {
        name.set("kontainer")
        description.set("kontainer - A simple DI container written in Kotlin.")
        url.set("https://github.com/aryumka/kontainer/")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        scm {
            url.set("https://github.com/aryumka/kontainer/")
            connection.set("https://github.com/aryumka/kontainer.git")
        }
        developers {
            developer {
                name.set("Aryumka")
                email.set("yooooar@gmail.com")
            }
        }
    }
}
