plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.3.1"
}

group = "com.hanyoonsoo"
version = "0.0.1-SNAPSHOT"
description = "spring-ai-playground"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

extra["springAiVersion"] = "1.0.0"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/release") }
}

dependencies {
    // spring boot & kotlin
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // PostgresSQL
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.pgvector:pgvector:0.1.6")

    // P6Spy
    implementation("p6spy:p6spy:3.9.1")
    implementation("com.github.gavlyukovskiy:datasource-decorator-spring-boot-autoconfigure:1.9.0")

    // flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql:11.3.1")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    // PDFBox
    implementation("org.apache.pdfbox:pdfbox:2.0.30")

    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Spring AI 의존성
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation ("org.springframework.ai:spring-ai-starter-vector-store-pgvector")


    // macos
    if (isAppleSilicon()) {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.117.Final:osx-aarch_64")
    }

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

fun isAppleSilicon() = System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "aarch64"

tasks.withType<Test> {
    useJUnitPlatform()
}
