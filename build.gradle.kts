group = "kf"
version = "1.0"

plugins {
    kotlin("jvm") version "2.1.20"
//    id("org.jetbrains.dokka") version "2.0.0"
    id("io.ktor.plugin") version "3.1.1"
}
kotlin { jvmToolchain(21) }
repositories { mavenCentral() }
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-cio")
    implementation("org.apache.commons:commons-text:1.13.0")
    implementation("org.jline:jline-terminal:3.29.0")
    implementation("org.jline:jline-reader:3.29.0")
    implementation("org.jline:jline-console:3.29.0")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")  // silence warn
}

application {
    mainClass = "kf.cli.MainKt"
}

tasks.test { useJUnitPlatform() }
tasks.jar {
    manifest { attributes["Main-Class"] = "kf.cli.MainKt" }
}
