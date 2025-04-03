import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

group = "kf"
version = "1.0"

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.dokka") version "2.0.0"
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
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")
    implementation("org.apache.commons:commons-text:1.13.0")
    implementation("org.jline:jline-terminal:3.29.0")
    implementation("org.jline:jline-reader:3.29.0")
    implementation("org.jline:jline-console:3.29.0")
}


tasks.test { useJUnitPlatform() }
tasks.jar {
    manifest { attributes["Main-Class"] = "kf.cli.MainKt" }
}

tasks.register<Jar>("interfacesJar") {
    archiveClassifier.set("interfaces")
    from(sourceSets.main.get().output) {
        include("kf/interfaces/**")
        include("kf/dict/Word.class")
    }
}
application { mainClass.set("kf.cli.MainKt") }
dokka {
    dokkaSourceSets.main {
        documentedVisibilities(
            VisibilityModifier.Private,
            VisibilityModifier.Protected,
            VisibilityModifier.Package,
            VisibilityModifier.Public)
    }
}
