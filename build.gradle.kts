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
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.4")
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    testImplementation("com.codeborne:selenide:7.10.0")

    implementation("io.qameta.allure:allure-java-commons:2.29.1")
    implementation("io.qameta.allure:allure-commandline:2.35.1")
    implementation("io.qameta.allure:allure-rest-assured:2.29.1")

    implementation("io.qameta.allure:allure-junit5:2.29.1")
    implementation("io.qameta.allure:allure-assertj:2.29.1")
    implementation("io.qameta.allure:allure-selenide:2.29.1")

    implementation("javax.mail:mail:1.4.7")
    implementation("io.qameta.allure:allure-maven:2.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.13.4")
    implementation("commons-io:commons-io:2.20.0")

}

tasks.test {
    useJUnitPlatform()
}