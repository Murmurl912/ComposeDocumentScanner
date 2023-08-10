import com.android.build.api.dsl.Packaging
import org.jetbrains.kotlin.cli.jvm.main

plugins {
    id("com.android.library")
}


val openCVersionName = "4.8.0"
val openCVersionCode = ((4 * 100 + 8) * 100 + 0) * 10 + 0


android {
    namespace = "org.opencv"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        getByName("main") {
            java.srcDir("java")
            manifest.srcFile("java/AndroidManifest.xml")
            jniLibs.srcDir("native/libs")
        }
    }
    externalNativeBuild {
        cmake {
            path (project.projectDir.toString() + "/libcxx_helper/CMakeLists.txt")
        }
    }


    buildTypes {
        debug {
            packaging {
                doNotStrip("**/*.so")  // controlled by OpenCV CMake scripts
            }
        }
        release {
            packaging {
                doNotStrip("**/*.so'")  // controlled by OpenCV CMake scripts
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt"
            )
        }
    }

}

dependencies {

}