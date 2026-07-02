plugins {
    alias(libs.plugins.agp.lib)
}

val androidTargetSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidBuildToolsVersion: String by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidNdkVersion: String by rootProject.extra
val androidCmakeVersion: String by rootProject.extra

android {
    namespace = "com.laole918.yare"
    compileSdk = androidCompileSdkVersion
    ndkVersion = androidNdkVersion
    buildToolsVersion = androidBuildToolsVersion

    buildFeatures {
        prefab = true
    }

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        consumerProguardFiles("consumer-rules.pro")

        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_STL=none")
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
            path = file("src/main/jni/CMakeLists.txt")
            version = androidCmakeVersion
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(libs.cxx)
}
