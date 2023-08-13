plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        getByName("main") {
            java.srcDir("java/src")
            res.srcDir("java/res")
            manifest.srcFile("java/AndroidManifest.xml")
            jniLibs.srcDir("native/libs")
            aidl.srcDir("java/src")
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