plugins {
    id("com.android.application") apply false
    kotlin("android") apply false
}

allprojects {
    repositories {
        google()
        gradlePluginPortal()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    }
}

tasks.getByName<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
