plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Module dependencies
    api(project(":waveCoach:domain"))

    // To use Kotlin specific date and time functions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}