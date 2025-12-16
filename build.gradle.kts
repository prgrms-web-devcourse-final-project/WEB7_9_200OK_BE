plugins {
    java
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "windfall"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    //implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")


    //querydsl
    annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:7.1:jpa")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:7.1")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

// JSON 파싱

    // websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")


}

tasks.withType<Test> {
    useJUnitPlatform()
}


//-----------querydsl-----------//
val querydslDir = file("src/main/generated")

sourceSets {
    main {
        java.srcDir(querydslDir)
    }
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(querydslDir)
}

tasks.named("clean") {
    doLast {
        querydslDir.deleteRecursively()
    }
}
//--------------------------------//