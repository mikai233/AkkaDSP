import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

plugins {
    kotlin("jvm") version "1.6.10"
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
        implementation("com.typesafe.akka:akka-actor_$scalaVersion:$akkaVersion")
        implementation("com.typesafe.akka:akka-cluster_$scalaVersion:$akkaVersion")
        implementation("com.typesafe.akka:akka-cluster-sharding_$scalaVersion:$akkaVersion")
        implementation("com.typesafe.akka:akka-slf4j_$scalaVersion:$akkaVersion")
        implementation("io.altoo:akka-kryo-serialization_$scalaVersion:2.3.0") {
            exclude("com.typesafe.akka", "akka-actor_2.13")
        }
        implementation("ch.qos.logback:logback-classic:1.2.10")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
        runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxVersion")

        testImplementation(kotlin("test"))
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