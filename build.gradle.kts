val ktorVersion = "2.0.0-beta-1"
val kotlinVersion = "1.6.10"
val logbackVersion = "1.2.11"
val postgresqlVersion = "42.3.3"
val exposedVersion = "0.37.3"
val flywayVersion = "8.5.4"
val jbcryptVersion = "0.4"


plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    id("org.flywaydb.flyway") version "8.5.4"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "ackuq.github.io"
version = "0.0.1"
application {
    mainClass.set("io.github.ackuq.ApplicationKt.module")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Core packets
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    // Typesafe routing
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    // JSON serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    // CORS support
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    // Status pages
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    // Authentication
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    // Logback
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // BCrypt
    implementation("org.mindrot:jbcrypt:$jbcryptVersion")
    // Postgres connector
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    // Exposed as SQL framework
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    // Flyway for DB migrations
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    // Tests
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}