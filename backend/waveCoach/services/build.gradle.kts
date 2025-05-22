plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
//    id("io.spring.dependency-management")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":waveCoach:domain"))
    implementation(project(":waveCoach:repository"))

    // To get the DI annotation
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // To use Kotlin specific date and time functions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // To use SLF4J
    implementation("org.slf4j:slf4j-api:2.0.16")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // To use the JDBI-based repository implementation on the tests
    testImplementation(project(":waveCoach:repository-jdbi"))
    testImplementation("org.jdbi:jdbi3-core:3.37.1")
    testImplementation("org.postgresql:postgresql:42.7.2")

    // Cloudinary
    implementation("com.cloudinary:cloudinary-http44:1.32.0")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    if (System.getenv("DB_URL") == null) {
        environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
    }
    dependsOn(":waveCoach:repository-jdbi:dbTestsWait")
    finalizedBy(":waveCoach:repository-jdbi:dbTestsDown")
}

kotlin {
    jvmToolchain(21)
}
