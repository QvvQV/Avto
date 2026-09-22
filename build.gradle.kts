plugins {
    java
}

group = "ru.tutor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

    // REST Assured
    testImplementation("io.rest-assured:rest-assured:6.0.1")
    testImplementation("commons-codec:commons-codec:1.22.1")
    testImplementation("tools.jackson.core:jackson-databind:3.2.2")

    // Selenide
    testImplementation("com.codeborne:selenide:7.18.0")

    // Playwright
    testImplementation("com.microsoft.playwright:playwright:1.47.0")


//    implementation("io.qameta.allure:allure-commandline:2.35.1")

    // Allure
    testImplementation("io.qameta.allure:allure-jupiter:2.35.4")
    testImplementation("io.qameta.allure:allure-assertj:2.35.4")
    testImplementation("io.qameta.allure:allure-selenide:2.35.4")
    testImplementation("io.qameta.allure:allure-rest-assured:2.35.4")
    testImplementation("io.qameta.allure:allure-java-commons:2.35.4")

    // Для playwright
//    testImplementation("io.qameta.allure:allure-playwright:2.32.0")

    implementation("jakarta.mail:jakarta.mail-api:2.1.5")
    implementation("org.eclipse.angus:angus-mail:2.0.5")

    //JUnit 5
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testImplementation("org.junit.platform:junit-platform-launcher:6.1.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.1.3")

    // Utils
    implementation("commons-io:commons-io:2.22.0")
    implementation("net.datafaker:datafaker:2.7.0")
    implementation("org.json:json:20260814")
    implementation("org.jsoup:jsoup:1.23.2")

}

tasks.test {
    useJUnitPlatform()
}