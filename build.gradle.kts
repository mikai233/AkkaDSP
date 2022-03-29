import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.20-RC2"
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
}

allprojects {
    afterEvaluate {
        configureJvmTarget()
        configureKotlinExperimentalUsages()
    }
}

val akkaVersion = "2.6.18"
val scalaVersion = "2.13"
val kotlinVersion = "1.6.20-M1"
val kotlinxVersion = "1.6.0"

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-allopen")
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

val experimentalAnnotations = arrayOf(
    "kotlin.RequiresOptIn",
)

fun KotlinSourceSet.configureKotlinExperimentalUsages() {
    languageSettings.progressiveMode = true
    experimentalAnnotations.forEach { a ->
        languageSettings.optIn(a)
    }
}

fun Project.configureKotlinExperimentalUsages() {
    val kotlinExtension = extensions.findByName("kotlin") as? org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
    val sourceSets = kotlinExtension?.sourceSets ?: return

    for (target in sourceSets) {
        target.configureKotlinExperimentalUsages()
    }
}