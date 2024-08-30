plugins {
    id("java")
}

group = "com.github.imaqtkatt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}