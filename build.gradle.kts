import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

plugins {
    kotlin("jvm") version "1.6.20-RC"
}

group = "com.mikai233"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
}

allprojects {
    afterEvaluate {
        configureJvmTarget()
    }
}

val akkaVersion = "2.6.18"
val scalaVersion = "2.13"
val kotlinVersion = "1.6.20-M1"
val kotlinxVersion = "1.6.0"

subprojects {
    apply(plugin = "kotlin")
    dependencies {
        testImplementation(kotlin("test"))
    }
    repositories {
        mavenCentral()
    }
}

fun Project.configureJvmTarget() {
    tasks.withType<JavaCompile> {
        with(options) {
            encoding = "UTF-8"
            isFork = true
        }
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            javaParameters = true
        }
    }
}