
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.core.ads"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation("com.google.android.gms:play-services-ads:25.2.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")

    implementation("androidx.lifecycle:lifecycle-process:2.10.0")

    implementation(platform(libs.androidx.compose.bom))

    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.ui:ui")

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.kotlinx.serialization.json)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}