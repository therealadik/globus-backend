plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

group = "com.example"
version = "1.0"

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    /**
     * JWT
     */
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    /**
     * Swagger / OpenAPI
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    /**
     * PDF Generation
     */
    implementation("org.apache.pdfbox:pdfbox:3.0.3")
    implementation("org.apache.pdfbox:fontbox:3.0.3")
    implementation("org.apache.pdfbox:xmpbox:3.0.3")
    implementation("org.apache.pdfbox:preflight:3.0.3")
    implementation("org.apache.pdfbox:pdfbox-tools:3.0.3")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:5.2.0")
    runtimeOnly("org.postgresql:postgresql")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    /**
     * Test containers
     */
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf(
                "com.example.globus.service.*",
                "com.example.globus.service",
                "com.example.globus.security"
            )
            excludes = listOf(
                "com.example.globus.service.transaction.filter",
            )

            limit {
                minimum = BigDecimal.valueOf(0.7)
            }
        }
    }
}
