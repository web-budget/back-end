import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3


plugins {
    // spring
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"

    // detekt
    id("dev.detekt") version "2.0.0-alpha.2"

    // kotlin things
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    kotlin("plugin.jpa") version "2.3.0"
}

group = "br.com.webbudget"
version = "4.0.0"

java.sourceCompatibility = JavaVersion.VERSION_25

repositories {
    mavenCentral()
}

val testcontainersVersion = "2.0.2"
val guavaVersion = "33.5.0-jre"
val assertJVersion = "3.27.7"
val mockkVersion = "5.0.1"
val jsonUnitVersion = "5.1.0"
val awaitilityVersion = "4.3.0"
val hypersistenceUtilsVersion = "3.15.1"
val greenMailVersion = "2.1.3"
val kotlinLoggingJvmVersion = "7.0.14"

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // utilities
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingJvmVersion")
    implementation("io.hypersistence:hypersistence-utils-hibernate-71:$hypersistenceUtilsVersion")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // database
    runtimeOnly("org.postgresql:postgresql")

    // testing
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")

    testImplementation("org.assertj:assertj-core:$assertJVersion")

    testImplementation("com.ninja-squad:springmockk:$mockkVersion")

    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
    testImplementation("net.javacrumbs.json-unit:json-unit-spring:$jsonUnitVersion")

    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testImplementation("org.awaitility:awaitility-kotlin:$awaitilityVersion")

    testImplementation("com.icegreen:greenmail-junit5:$greenMailVersion")

    // testcontainers
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
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

    bootBuildImage {
        environment.put("BP_JVM_VERSION", JVM_25.target)
        environment.put("BPE_DELIM_JAVA_TOOL_OPTIONS", " ")
        environment.put(
            "BPE_APPEND_JAVA_TOOL_OPTIONS",
            "-XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC -XX:+UseStringDeduplication -Dfile.encoding=UTF-8 -Duser.timezone=UTC"
        )
        imageName.set("web-budget/${project.name}:v${project.version}")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JVM_25)
        languageVersion.set(KOTLIN_2_3)
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjdk-release=${java.sourceCompatibility}")
    }
}

springBoot {
    buildInfo {
        properties {
            group.set(project.group as String)
            version.set(project.version as String)
            artifact.set(project.name)

            description = "webBudget backend application"

            name.set("webBudget Backend")
        }
    }
}

tasks.withType<Detekt> {
    parallel = true
    buildUponDefaultConfig = true

    exclude("**/fixtures/**")
}