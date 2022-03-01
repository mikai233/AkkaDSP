group = "com.mikai233"
version = "1.0-SNAPSHOT"

dependencies {
    apply(plugin = "scala")
    implementation(akka.actor)
    implementation(akka.cluster)
    implementation(akka.cluster.sharding)

    implementation(lib.kotlinx.coroutines.core)
    implementation(lib.kotlinx.coroutines.core.jvm)
    implementation(lib.kotlinx.coroutines.jdk8)
}