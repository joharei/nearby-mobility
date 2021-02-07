import de.fayard.refreshVersions.bootstrapRefreshVersions

rootProject.name = "Nearby Mobility"
include(":common")
include(":app")

pluginManagement {
    repositories {
        jcenter()
        google()
        gradlePluginPortal()
    }
}

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions(
    extraArtifactVersionKeyRules = listOf(
        file("refreshVersions-extra-rules.txt").readText()
    )
)
