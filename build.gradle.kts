plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.workledger"
version = "0.0.1-SNAPSHOT"
description = "WorkLedger is an enterprise-grade work tracking and reconciliation platform designed to align delivery systems (Jira), employee effort, and billing narratives with audit-ready transparency."

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
    /* -------------------- Spring Boot -------------------- */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    /* -------------------- Database -------------------- */
    runtimeOnly("org.postgresql:postgresql")

    /* -------------------- Lombok -------------------- */
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    /* -------------------- MapStruct -------------------- */
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    /* -------------------- OpenAPI -------------------- */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")

    /* -------------------- Testing -------------------- */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
