plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)

    alias(libs.plugins.google.services)
}

android {
    namespace = "com.all.video.downloader.fast.hd.secure.video.downloader"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    defaultConfig {
        applicationId = "com.all.video.downloader.fast.hd.secure.video.downloader"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }

  /*      buildConfigField(
            type = "String",
            name = "DOWNLOADER_BASE_URL",
            value = "\"https://testingdownloader.totalfreeai.com/\""
        )

        buildConfigField(
            type = "String",
            name = "DOWNLOADER_SECRET_KEY",
            value = "\"I3V1T9kAd7iD0jg7ITqQLgjcZC1Nv7cyO3WZILtHsYhXVumkPj\""
        )*/

        //test id
      /*  manifestPlaceholders["ADMOB_APPLICATION_ID"] =
            "ca-app-pub-3940256099942544~3347511713"*/


        //real admob id
        manifestPlaceholders["ADMOB_APPLICATION_ID"] =
            "ca-app-pub-3484666557551870~5506879050"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "DebugProbesKt.bin"
        }
    }
}

dependencies {

    implementation(project(":core:ads"))

    /**
     * Google Play Billing
     */
    implementation(libs.google.play.billing)

    /**
     * Firebase
     */
    implementation(platform(libs.firebase.bom))
     implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.kotlinx.coroutines.play.services)

    /**
     * Core Android
     */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    /**
     * Compose
     */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    /**
     * Lifecycle / ViewModel / State collection
     */
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    /**
     * Navigation 3
     */
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)


    /**
     * Dependency Injection - Hilt
     */
    implementation(libs.hilt.android)
    implementation(libs.material)
    ksp(libs.hilt.compiler)

    /**
     * Hilt integration with Compose / ViewModel / WorkManager
     */
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    /**
     * Networking - Ktor
     */
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    /**
     * Kotlinx Serialization
     */
    implementation(libs.kotlinx.serialization.json)

    /**
     * Coroutines
     */
    implementation(libs.kotlinx.coroutines.android)

    /**
     * Background downloading
     */
    implementation(libs.androidx.work.runtime.ktx)

    /**
     * Local database
     */
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    /**
     * Preferences / Settings
     */
    implementation(libs.androidx.datastore.preferences)

    /**
     * Image loading
     */
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    /**
     * Video preview / playback
     */
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media)

    /**
     * Logging
     */
    implementation(libs.timber)

    /**
     * Unit testing
     */
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.turbine)

    /**
     * Android testing
     */
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    /**
     * Debug tooling
     */
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    /**
     * extended icons
     */
    implementation("androidx.compose.material:material-icons-extended")

    /**
    * coil
     */
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-video:2.7.0")


    /**
     * koin
     */
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)


    implementation(libs.androidx.biometric)


    implementation(libs.androidx.fragment.ktx)


    implementation(libs.androidx.documentfile)

}