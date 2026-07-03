plugins {
    alias(libs.plugins.agp.app)
}

val androidTargetSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidBuildToolsVersion: String by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidCmakeVersion: String by rootProject.extra

android {
    namespace = "com.laole918.yare.sample"
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.laole918.yare.sample"
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        versionCode = 1
        versionName = "1.0"

        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++20")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = androidCmakeVersion
        }
    }
}

dependencies {
    implementation(project(":yare"))
}
