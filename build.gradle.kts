group = "kf"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.1.10"
//    id("org.jetbrains.dokka") version "2.0.0"
    id("io.ktor.plugin") version "3.1.1"
}
kotlin { jvmToolchain(21) }
repositories { mavenCentral() }
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("com.github.ajalt.mordant:mordant:3.0.2")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-cio")
}

application {
    mainClass = "kf.MainKt"
}

tasks.test { useJUnitPlatform() }
tasks.jar {
    manifest { attributes["Main-Class"] = "kf.MainKt" }
}
