import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // spring
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"

    // detekt
    id("io.gitlab.arturbosch.detekt") version "1.23.8"

    // kotlin things
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"

    // mapstruct
    kotlin("kapt") version "2.0.21"
}

group = "br.com.webbudget"
version = "4.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

val testcontainersVersion = "1.21.0"
val guavaVersion = "33.4.8-jre"
val mapstructVersion = "1.6.3"
val assertJVersion = "3.27.3"
val mockkVersion = "4.0.2"
val jsonUnitVersion = "4.1.1"
val awaitilityVersion = "4.3.0"
val hypersistenceUtilsVersion = "3.9.10"
val kotlinLoggingJvmVersion = "7.0.7"
val greenMailVersion = "2.1.3"

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
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:$hypersistenceUtilsVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingJvmVersion")

    // mapstruct
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")

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
        languageVersion.set(KOTLIN_2_0)
        freeCompilerArgs.set(
            listOf(
                "-Xjsr305=strict",
                "-Xjdk-release=${java.sourceCompatibility}"
            )
        )
    }
}

tasks.withType<Detekt> {
    parallel = true
    buildUponDefaultConfig = true

    exclude("**/fixtures/**")
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
        environment.put("BP_JVM_VERSION", "21")
        environment.put("BPE_DELIM_JAVA_TOOL_OPTIONS", " ")
        environment.put(
            "BPE_APPEND_JAVA_TOOL_OPTIONS",
            "-XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC -XX:+UseStringDeduplication -Dfile.encoding=UTF-8 -Duser.timezone=UTC"
        )
        imageName.set("web-budget/${project.name}:v${project.version}")
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
