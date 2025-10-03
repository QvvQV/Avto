plugins {
    id("java")
}

group = "ru.tutor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

    // REST Assured
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")

    // Selenide
    testImplementation("com.codeborne:selenide:7.10.0")

//    testImplementation("io.qameta.allure:allure-java-commons:2.29.1")
//    implementation("io.qameta.allure:allure-commandline:2.35.1")

    // Allure
    testImplementation("io.qameta.allure:allure-junit5:2.29.1")
    testImplementation("io.qameta.allure:allure-assertj:2.29.1")
    testImplementation("io.qameta.allure:allure-selenide:2.29.1")
    testImplementation("io.qameta.allure:allure-rest-assured:2.29.1")

    implementation("javax.mail:mail:1.4.7")


    //JUnit 5
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.4")
    testImplementation("org.junit.platform:junit-platform-launcher:1.13.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.13.4")

    // Utils
    implementation("commons-io:commons-io:2.20.0")
    implementation("net.datafaker:datafaker:2.4.4")
    implementation("org.json:json:20250517")

    implementation("org.jsoup:jsoup:1.21.2")

//    implementation("io.qameta.allure:allure-maven:2.15.2")
}

tasks.test {
    useJUnitPlatform()
}