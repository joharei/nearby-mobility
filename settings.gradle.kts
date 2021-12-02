rootProject.name = "Nearby Mobility"
include(":common")
include(":app")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.23.0"
}

refreshVersions {
    extraArtifactVersionKeyRules = listOf(
        file("refreshVersions-extra-rules.txt").readText()
    )
    rejectVersionIf {
        candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
    }
}
