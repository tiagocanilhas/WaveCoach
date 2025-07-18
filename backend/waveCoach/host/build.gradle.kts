plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Module dependencies
    implementation(project(":waveCoach:domain"))
    implementation(project(":waveCoach:http"))
    implementation(project(":waveCoach:services"))
    implementation(project(":waveCoach:repository-jdbi"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // for JDBI and Postgres
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")

    // To use Kotlin specific date and time functions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // To get password encode
    implementation("org.springframework.security:spring-security-core:6.3.0")

    // To use WebTestClient on tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation(kotlin("test"))

    // Cloudinary
    implementation("com.cloudinary:cloudinary-http44:1.32.0")
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

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("wavecoach")
    archiveVersion.set("")
    archiveClassifier.set("")
    dependsOn("copyRuntimeDependencies")
    manifest {
        attributes["Main-Class"] = "waveCoach.host.WaveCoachApplicationKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    }
}

tasks.register("stage") {
    dependsOn("assemble", "copyRuntimeDependencies")
}
