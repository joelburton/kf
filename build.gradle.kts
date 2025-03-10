plugins {
    kotlin("jvm") version "2.1.10"
}

group = "kf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kf.MainKt"
    }

}


tasks.register<Jar>("uberJar") {
    manifest {
        attributes["Main-Class"] = "kf.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveClassifier = "uber"

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map { zipTree(it) }
    })
}
kotlin {
    jvmToolchain(23)
}