plugins { `kotlin-dsl` }

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(
        "org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:${embeddedKotlinVersion}"
    )
    implementation(libs.dokka.plugin)
    implementation(libs.spotless.plugin)
}

kotlin { jvmToolchain(11) }
