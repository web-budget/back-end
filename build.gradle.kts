import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // spring
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"

    // detekt
    id("io.gitlab.arturbosch.detekt") version "1.23.5"

    // kotlin things
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"

    // mapstruct
    kotlin("kapt") version "1.9.22"
}

group = "br.com.webbudget"
version = "4.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val testcontainersVersion = "1.19.7"
val guavaVersion = "33.0.0-jre"
val mapstructVersion = "1.5.5.Final"
val assertJVersion = "3.25.3"
val mockkVersion = "4.0.2"
val jsonUnitVersion = "3.2.7"
val awaitilityVersion = "4.2.0"
val hypersistentceUtilsVersion = "3.7.3"
val kotlinLoggingJvmVersion = "6.0.3"
val greenMailVersion = "2.0.1"
val arrowVersion = "1.2.3"

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // utilities
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:$hypersistentceUtilsVersion")

    // mapstruct
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingJvmVersion")

    // dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")

    // database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    // testing
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.mockito", "mockito-core")
        exclude("org.junit.vintage", "junit-vintage-engine")
    }

    testImplementation("org.assertj:assertj-core:$assertJVersion")

    testImplementation("com.ninja-squad:springmockk:$mockkVersion")

    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-spring:$jsonUnitVersion")

    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testImplementation("org.awaitility:awaitility-kotlin:$awaitilityVersion")

    testImplementation("com.icegreen:greenmail-junit5:$greenMailVersion")

    // testcontainers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JVM_21)
        languageVersion.set(KOTLIN_1_9)
        freeCompilerArgs.set(
            listOf(
                "-Xjsr305=strict",
                "-Xjdk-release=${java.sourceCompatibility}"
            )
        )
    }
}

tasks.withType<Detekt> {
    exclude("**/fixture/**")
}

tasks {
    test {
        useJUnitPlatform()
    }
    bootJar {
        layered {
            enabled.set(true)
            application {
                intoLayer("spring-boot-loader") {
                    include("org/springframework/boot/loader/**")
                }
                intoLayer("application")
            }
            dependencies {
                intoLayer("application") {
                    includeProjectDependencies()
                }
                intoLayer("snapshot-dependencies") {
                    include("*:*:*SNAPSHOT")
                }
                intoLayer("dependencies")
            }
            layerOrder.set(listOf("dependencies", "spring-boot-loader", "snapshot-dependencies", "application"))
        }
        archiveFileName.set("${project.name}.${archiveExtension.get()}")
    }
}

springBoot {
    buildInfo {
        properties {
            group.set(project.group as String)
            version.set(project.version as String)
            artifact.set("back-end")

            description = "webBudget backend application"

            name.set("webBudget Backend")
        }
    }
}
