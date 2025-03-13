group = "kf"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.1.10"
//    id("org.jetbrains.dokka") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.6"

}
kotlin { jvmToolchain(17) }
repositories { mavenCentral() }
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("com.github.ajalt.mordant:mordant:3.0.2")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
}

tasks.test { useJUnitPlatform() }
tasks.jar {
    manifest { attributes["Main-Class"] = "kf.MainKt" }
}
//tasks.register<Jar>("uberJar") {
//    manifest { attributes["Main-Class"] = "kf.MainKt" }
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//    archiveClassifier = "uber"
//    from(sourceSets.main.get().output)
//    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get().filter {
//            it.name.endsWith("jar")
//        }.map { zipTree(it) }
//    })
//}

