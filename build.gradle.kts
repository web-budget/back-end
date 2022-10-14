import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // spring
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"

    // detekt
    id("io.gitlab.arturbosch.detekt").version("1.21.0")

    // kotlin things
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.spring") version "1.7.20"
    kotlin("plugin.jpa") version "1.7.20"

    // mapstruct
    kotlin("kapt") version "1.7.20"
}

group = "br.com.webbudget"
version = "4.0.0"

java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val testcontainersVersion = "1.17.5"
val guavaVersion = "31.1-jre"
val mapstructVersion = "1.5.3.Final"
val mapstructExtVersion = "0.1.1"
val auth0Version = "4.1.0"
val assertJVersion = "3.23.1"
val mockkVersion = "3.1.1"

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // utilities
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("com.auth0:java-jwt:$auth0Version")

    // mapstruct
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    kapt("org.mapstruct.extensions.spring:mapstruct-spring-extensions:$mapstructExtVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.mapstruct.extensions.spring:mapstruct-spring-annotations:$mapstructExtVersion")

    // dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

    // database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.mockito", "mockito-core")
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("com.ninja-squad:springmockk:$mockkVersion")

    // testcontainers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

kapt {
    arguments {
        arg("mapstruct.verbose", "false")
        arg("mapstruct.suppressGeneratorTimestamp", "true")
        arg("mapstruct.suppressGeneratorVersionInfoComment", "true")
    }
}

springBoot {
    buildInfo {
        properties {
            group = group
            version = version
            artifact = "web-budget_backend"
            name = "webBudget backend application"
        }
    }
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
