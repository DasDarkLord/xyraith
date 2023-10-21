import java.nio.file.Paths

val majorVersion = 0
val minorVersion = 2
val patchVersion = 0

plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "net.realmofuz"
version = "0.2.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.hollow-cube:minestom-ce:010fe985bb")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    implementation("de.articdive:jnoise-pipeline:4.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("cc.ekblad:4koma:1.2.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.reflections:reflections:0.10.2")
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

/*
Generate the standard library from the source files
 */
fun generateStdlib(): String {
    var output = "package stdlib\n\n//Automatically generated in `build.gradle.kts`\n\n"
    output += "val stdlibFiles = mutableMapOf<String, String>(\n"
    File("${project.rootDir.path}/std/").walk().forEach {
        if(it.isFile && !it.isDirectory) {
            val path = it.canonicalPath
                .replace(project.rootDir.path + "\\", "")
                .replace("\\", "/")
                .removeSuffix(".xr")
            output += "\"${path}\" to \"\"\"${it.readText()}\"\"\"\n,"
        }
    }
    output += "\n)"
    return output
}

File("${project.rootDir.path}/src/main/kotlin/stdlib/Stdlib.kt").writeText(generateStdlib())
