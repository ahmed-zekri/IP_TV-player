plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt") // Use this direct reference instead of an alias
    alias(libs.plugins.hilt.android) // Add Hilt plugin
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.zekri_ahmed.ip_tv_player"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zekri_ahmed.ip_tv_player"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Add Media3 ExoPlayer core and HLS extension
    implementation (libs.androidx.media3.exoplayer)  // Use the latest version
    implementation (libs.androidx.media3.exoplayer.hls) // HLS extension
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.jaffree)
    implementation(libs.androidx.media3.ui)
    // Add Hilt dependencies
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.media3.session)
    implementation(libs.hilt.navigation.compose) // For Compose integration
    implementation (libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}
kapt {
    correctErrorTypes = true
}