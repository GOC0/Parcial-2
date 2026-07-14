plugins {
    id("java")
    id("application")
}

group = "frame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application{
    mainClass.set("Main")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    implementation("io.javalin:javalin:7.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.10")

    implementation("org.jsoup:jsoup:1.15.3")

    implementation("com.fasterxml.jackson.core:jackson-core:2.21.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")

    implementation("io.javalin:javalin-rendering-thymeleaf:7.2.2")

    implementation ("org.hibernate.orm:hibernate-core:7.4.0.Final")

    implementation("com.h2database:h2:2.4.240")
    implementation("org.postgresql:postgresql:42.7.11")

    implementation ("com.auth0:java-jwt:4.5.2")
}

tasks.test {
    useJUnitPlatform()
}