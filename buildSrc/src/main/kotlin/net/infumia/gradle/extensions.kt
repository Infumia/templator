package net.infumia.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.configureKotlin(
    javaVersion: Int = 8,
    sources: Boolean = true,
    javadoc: Boolean = true
) {
    apply<DokkaPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories.mavenCentral()

    extensions.configure<KotlinJvmProjectExtension> { jvmToolchain(javaVersion) }

    if (javadoc) {
        val javadocJar by
            tasks.creating(Jar::class) {
                dependsOn("javadoc")
                archiveClassifier.set("javadoc")
                from(javadoc)
            }
    }

    if (sources) {
        val sourceSets = extensions.getByType<JavaPluginExtension>().sourceSets
        val sourcesJar by
            tasks.creating(Jar::class) {
                dependsOn("classes")
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }
    }
}

// TODO: portlek, Implement the publishing to gradle portal.
fun Project.configurePublish(
    // moduleName: String? = null
) {
    // val projectName = "templator${if (moduleName == null) "" else "-$moduleName"}"
    // val signRequired = project.hasProperty("sign-required")
}

fun Project.configureSpotless() {
    val subProjects = subprojects.map { it.projectDir.toRelativeString(projectDir) }

    repositories.mavenCentral()

    apply<SpotlessPlugin>()

    extensions.configure<SpotlessExtension> {
        isEnforceCheck = false
        lineEndings = com.diffplug.spotless.LineEnding.UNIX

        val prettierConfig =
            mapOf(
                "prettier" to "3.3.3",
                "prettier-plugin-java" to "2.6.4",
                "prettier-plugin-toml" to "2.0.1",
            )

        yaml {
            target(".github/**/*.yml")
            endWithNewline()
            trimTrailingWhitespace()
            jackson().yamlFeature("LITERAL_BLOCK_STYLE", true).yamlFeature("SPLIT_LINES", false)
        }

        json {
            target("renovate.json")
            endWithNewline()
            trimTrailingWhitespace()
            jackson()
        }

        format("toml") {
            target("gradle/libs.versions.toml")
            endWithNewline()
            trimTrailingWhitespace()
            prettier(prettierConfig)
                .config(
                    mapOf(
                        "parser" to "toml",
                        "plugins" to listOf("prettier-plugin-toml"),
                    ),
                )
        }

        kotlin {
            target(
                "buildSrc/src/main/kotlin/**/*.kt",
                "buildSrc/**/*.gradle.kts",
                "*.gradle.kts",
                *subProjects.map { "$it/*.gradle.kts" }.toTypedArray(),
                *subProjects.map { "$it/src/main/kotlin/**/*.kt" }.toTypedArray(),
            )
            endWithNewline()
            trimTrailingWhitespace()
            ktfmt().kotlinlangStyle().configure {
                it.setMaxWidth(100)
                it.setBlockIndent(4)
                it.setContinuationIndent(4)
                it.setRemoveUnusedImport(true)
            }
        }
    }
}
