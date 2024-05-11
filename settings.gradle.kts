rootProject.name = "Nearby Mobility"

include(":common")
include(":app")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
}

refreshVersions {
    extraArtifactVersionKeyRules = listOf(
        file("refreshVersions-extra-rules.txt").readText()
    )
    rejectVersionIf {
        candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
    }
}
