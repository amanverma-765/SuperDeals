import java.net.URI

plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.ark"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenCentral()
    mavenLocal()
    google()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.ark:oak:1.0")
    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.2.0")
    implementation("io.ktor:ktor-client-core:3.0.2")
    implementation("io.ktor:ktor-client-okhttp:3.0.2")
    implementation("io.ktor:ktor-client-okhttp-jvm:3.0.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}