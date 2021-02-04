plugins {
    id("com.android.application") apply false
    id("org.jetbrains.kotlin.android") apply false
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

tasks.getByName<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
