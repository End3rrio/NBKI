plugins {
    id ("java")
    id ("org.springframework.boot") version "3.1.3"
    id ("io.spring.dependency-management") version "1.1.3"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //Spring
    implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    //H2
    runtimeOnly ("com.h2database:h2")
    //Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")

    implementation ("jakarta.validation:jakarta.validation-api:3.1.0")

    //Tests
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("org.apache.commons:commons-lang3:3.17.0")
    testImplementation ("org.springframework.boot:spring-boot-starter-test:3.3.5")

    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}