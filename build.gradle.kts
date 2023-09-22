plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "me.endistic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.hollow-cube:minestom-ce:010fe985bb")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}