import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(17)
    js {
        browser {
            commonWebpackConfig {
                outputFileName = "translate.js"
                sourceMaps = false
            }
        }
        binaries.executable()
        useCommonJs()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlinWrappers.react)
            }
        }
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        target.set("es2015")
    }
}