plugins {
    id("com.android.application") apply false
    kotlin("android") apply false
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
    }
}

tasks.getByName<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
