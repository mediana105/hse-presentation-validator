plugins {
    java
    application
    kotlin("jvm")
}

group = "org.hse.validator"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.whathecolor")
        }
    }
}

dependencies {
    // POI без Log4j
    implementation("org.apache.poi:poi-ooxml:5.4.0") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    }
    implementation("org.apache.poi:poi:5.2.3") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    }
    implementation("org.jetbrains:annotations:24.0.1")

    implementation("org.apache.logging.log4j:log4j-core:2.17.2")

    implementation("com.ibm.icu:icu4j:72.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
}

application {
    mainClass.set("org.hse.validator.Main")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}