@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "AkkaDSP"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("lib") {
            val kotlinVersion = "1.6.20-RC"
            val kspVersion = "1.0.2"
            version("kotlin", kotlinVersion)
            version("kotlinx", "1.6.0")
            version("kotlinx-serialization", "1.3.2")
            version("ksp", "$kotlinVersion-$kspVersion")
            version("scala", "2.13.8")

            alias("kotlin.stdlib").to("org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
            alias("kotlin.stdlib.jdk8").to("org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")
            alias("kotlinx.coroutines.core").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
                .versionRef("kotlinx")
            alias("kotlinx.coroutines.core.jvm").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm")
                .versionRef("kotlinx")
            alias("kotlinx.coroutines.jdk8").to("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8")
                .versionRef("kotlinx")
            alias("kotlinx.serialization.core.jvm").to(
                "org.jetbrains.kotlinx", "kotlinx-serialization-core-jvm"
            ).versionRef("kotlinx-serialization")
            alias("symbol.processing").to("com.google.devtools.ksp", "symbol-processing").versionRef("ksp")
            alias("symbol.processing.api").to("com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp")
            alias("scala").to("org.scala-lang", "scala-library").versionRef("scala")
            bundle(
                "kotlinx.coroutines",
                listOf("kotlinx.coroutines.core", "kotlinx.coroutines.core.jvm", "kotlinx.coroutines.jdk8")
            )
        }
        create("akka") {
            val akkaScalaVersion = "2.13"
            val akkaHttpVersion = "2.13"
            version("akka", "2.6.18")
            version("akka-http", "10.2.7")
            version("serialization", "2.3.0")
            version("logback-classic", "1.2.10")

            alias("actor").to("com.typesafe.akka", "akka-actor_$akkaScalaVersion").versionRef("akka")
            alias("cluster").to("com.typesafe.akka", "akka-cluster_$akkaScalaVersion").versionRef("akka")
            alias("cluster.sharding").to("com.typesafe.akka", "akka-cluster-sharding_$akkaScalaVersion")
                .versionRef("akka")
            alias("cluster.tools").to("com.typesafe.akka", "akka-cluster-tools_$akkaScalaVersion").versionRef("akka")
            alias("cluster.metrics").to("com.typesafe.akka", "akka-cluster-metrics_$akkaScalaVersion")
                .versionRef("akka")
            alias("stream").to("com.typesafe.akka", "akka-stream_$akkaScalaVersion").versionRef("akka")
            alias("http").to("com.typesafe.akka", "akka-http_$akkaHttpVersion").versionRef("akka-http")
            alias("serialization").to("io.altoo", "akka-kryo-serialization_$akkaScalaVersion")
                .versionRef("serialization")
            alias("log4j").to("com.typesafe.akka", "akka-slf4j_$akkaScalaVersion").versionRef("akka")
            alias("logback.classic").to("ch.qos.logback", "logback-classic").versionRef("logback-classic")
            bundle("log", listOf("log4j", "logback.classic"))
        }
        create("tools") {
            version("protobuf", "3.19.4")
            version("protobuf-plugin", "0.8.18")
            version("log4j-api", "1.7.35")
            version("curator", "5.2.0")
            version("netty", "4.1.74.Final")
            version("jackson", "2.13.1")
            version("snakeyaml", "1.30")
            version("guava", "31.0.1-jre")
            version("lz4", "1.8.0")
            version("bcprov", "1.70")
            version("druid", "1.2.8")
            version("ktorm", "3.4.1")
            version("kotlinpoet", "1.10.2")
            version("mysql", "8.0.28")

            alias("protoc").to("com.google.protobuf", "protoc").versionRef("protobuf")
            alias("protobuf").toPluginId("com.google.protobuf").versionRef("protobuf-plugin")
            alias("protobuf.kotlin").to("com.google.protobuf", "protobuf-kotlin").versionRef("protobuf")
            alias("protobuf.java").to("com.google.protobuf", "protobuf-java").versionRef("protobuf")
            alias("protobuf.java.util").to("com.google.protobuf", "protobuf-java-util").versionRef("protobuf")
            alias("log4j.api").to("org.slf4j", "slf4j-api").versionRef("log4j-api")
            alias("curator.recipes").to("org.apache.curator", "curator-recipes").versionRef("curator")
            alias("curator.framework").to("org.apache.curator", "curator-framework").versionRef("curator")
            alias("curator.async").to("org.apache.curator", "curator-x-async").versionRef("curator")
            alias("netty").to("io.netty", "netty-all").versionRef("netty")
            alias("netty.transport.native.epoll").to("io.netty", "netty-transport-native-epoll").versionRef("netty")
            alias("netty.transport.native.kqueue").to("io.netty", "netty-transport-native-kqueue").versionRef("netty")
            alias("jackson.databind").to("com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            alias("jackson.module.kotlin").to("com.fasterxml.jackson.module", "jackson-module-kotlin")
                .versionRef("jackson")
            alias("druid").to("com.alibaba", "druid").versionRef("druid")
            alias("ktorm.core").to("org.ktorm", "ktorm-core").versionRef("ktorm")
            alias("ktorm.support.mysql").to("org.ktorm", "ktorm-support-mysql").versionRef("ktorm")
            alias("guava").to("com.google.guava", "guava").versionRef("guava")
            alias("jackson.dataformat.yaml").to("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml")
                .versionRef("jackson")
            alias("lz4").to("org.lz4", "lz4-java").versionRef("lz4")
            alias("bcprov").to("org.bouncycastle", "bcpkix-jdk15on").versionRef("bcprov")
            alias("kotlinpoet").to("com.squareup", "kotlinpoet").versionRef("kotlinpoet")
            alias("kotlinpoet.ksp").to("com.squareup", "kotlinpoet-ksp").versionRef("kotlinpoet")
            alias("mysql.connector.java").to("mysql", "mysql-connector-java").versionRef("mysql")
            bundle("jackson", listOf("jackson.module.kotlin", "jackson.dataformat.yaml"))
            bundle("netty", listOf("netty", "netty.transport.native.epoll", "netty.transport.native.kqueue"))
        }
    }
}
include("common")
include("galaxy")
