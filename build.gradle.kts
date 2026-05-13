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
    testImplementation("io.rest-assured:rest-assured:6.0.0")
    testImplementation("commons-codec:commons-codec:1.21.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.21.0")

    // Selenide
    testImplementation("com.codeborne:selenide:7.16.0")


//    implementation("io.qameta.allure:allure-commandline:2.35.1")

    // Allure
    testImplementation("io.qameta.allure:allure-junit5:2.32.0")
    testImplementation("io.qameta.allure:allure-assertj:2.32.0")
    testImplementation("io.qameta.allure:allure-selenide:2.32.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.32.0")
    testImplementation("io.qameta.allure:allure-java-commons:2.32.0")

    implementation("javax.mail:mail:1.4.7")


    //JUnit 5
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.0-M1")
    testImplementation("org.junit.platform:junit-platform-launcher:6.1.0-M1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.1.0-M1")

    // Utils
    implementation("commons-io:commons-io:2.21.0")
    implementation("net.datafaker:datafaker:2.5.3")
    implementation("org.json:json:20251224")
    implementation("org.jsoup:jsoup:1.22.1")

//    implementation("io.qameta.allure:allure-maven:2.15.2")
}

tasks.test {
    useJUnitPlatform()
}