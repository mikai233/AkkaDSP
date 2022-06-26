import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.7.0"
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
}

group = "com.mikai233"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.3")
    implementation("com.typesafe.akka:akka-actor_2.13:2.6.19")
    implementation("com.typesafe.akka:akka-slf4j_2.13:2.6.19")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")
    implementation("io.netty:netty-all:4.1.78.Final")
    testImplementation(kotlin("test"))
}

allprojects {
    afterEvaluate {
        configureJvmTarget()
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
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
        }
    }
}