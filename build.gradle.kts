val ktorVersion = "2.0.2-eap-394"
val kotlinVersion = "1.6.10"
val logbackVersion = "1.2.11"
val postgresqlVersion = "42.3.3"
val exposedVersion = "0.37.3"
val flywayVersion = "8.5.4"
val jbcryptVersion = "0.4"
val hikariCPVersion = "5.0.1"
val h2Version = "2.1.210"
val caffeineVersion = "3.0.6"
val jsoupVersion = "1.14.3"
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
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Core packets
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    // HikariCP JDBC connection pool
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    // HTTP Client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    // Authentication
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    // Web Scraping
    implementation("org.jsoup:jsoup:$jsoupVersion")
    // Typesafe routing
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    // JSON serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    // CORS support
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    // Status pages
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    // Logback
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // Postgres connector
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    // Exposed as SQL framework
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    // Flyway for DB migrations
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    // Tests
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("com.h2database:h2:$h2Version")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    create("stage") {
        dependsOn("installDist")
    }
}

flyway {
    url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/partiguiden"
    user = System.getenv("DB_USER") ?: "partiguiden"
    password = System.getenv("DB_PASSWORD") ?: "secret_pass"
}

exposedCodeGeneratorConfig {
    configFilename = "exposed-generation-conf.yml"
    user = System.getenv("DB_USER") ?: "partiguiden"
    password = System.getenv("DB_PASSWORD") ?: "secret_pass"
    databaseName = "partiguiden"
    databaseDriver = "postgresql"
}
