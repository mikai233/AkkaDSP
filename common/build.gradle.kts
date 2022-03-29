group = "com.mikai233"
version = "1.0-SNAPSHOT"

dependencies {
    apply(plugin = "scala")
    //akka
    implementation(akka.actor)
    implementation(akka.cluster)
    implementation(akka.cluster.sharding)
    implementation(akka.bundles.log)
    //lib
    implementation(lib.bundles.kotlinx.coroutines)
    implementation(lib.kotlin.stdlib.jdk8)
    implementation(lib.kotlin.reflect)
    //tools
    implementation(tools.protobuf.kotlin)
    implementation(tools.reflections)
}