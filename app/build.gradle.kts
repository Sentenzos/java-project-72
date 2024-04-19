plugins {
    checkstyle
    jacoco
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.6"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.28")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.slf4j:slf4j-simple:2.0.13")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("gg.jte:jte:3.1.10")
    implementation("io.javalin:javalin:6.1.3")
    implementation("io.javalin:javalin-bundle:6.1.3")
    implementation("io.javalin:javalin-rendering:6.1.3")

}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass = "hexlet.code.App"
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}