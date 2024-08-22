package net.infumia.gradle

import org.gradle.api.Project

// TODO: portlek, Implement the publishing to gradle portal.
fun Project.publish(
    // moduleName: String? = null,
    javaVersion: Int = 8,
    sources: Boolean = true,
    javadoc: Boolean = true
) {
    applyCommon(javaVersion, sources, javadoc)

    // val projectName = "templator${if (moduleName == null) "" else "-$moduleName"}"
    // val signRequired = project.hasProperty("sign-required")
}
