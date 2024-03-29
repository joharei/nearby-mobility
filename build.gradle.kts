import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") apply false
    kotlin("android") apply false
}

allprojects {
    repositories {
        google()
        gradlePluginPortal()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = kotlinOptions.freeCompilerArgs +
                    "-opt-in=kotlin.RequiresOptIn"
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    afterEvaluate {
        plugins.withType<BasePlugin> {
            configure<BaseExtension> {
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }
            configure<KotlinAndroidProjectExtension> {
                jvmToolchain {
                    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
                }
            }
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.getByName<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
