import de.fayard.refreshVersions.bootstrapRefreshVersions

rootProject.name = "Nearby Mobility"
include(":app")

pluginManagement {
    repositories {
        jcenter()
        google()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()
