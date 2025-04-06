val koin_version = "3.2.0"

plugins {
    kotlin("kapt") version "1.9.0"
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vadym.birthday"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vadym.birthday"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        viewBinding = true
    }
    kapt {
        generateStubs = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":data"))
    implementation(project(":domain"))

    // View Model Life Cycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Koin DI
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    testImplementation(libs.koin.test.junit4)

    // Firebase
    implementation(libs.firebase.database.ktx.v2030)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.messaging)

    // GLIDE
    implementation(libs.glide)

    // Lottie Animation
    implementation(libs.lottie)

    // API
    implementation(libs.okhttp)

    // Work in background
    implementation (libs.androidx.work.runtime.ktx)


    // Ads
    implementation(libs.play.services.ads)

}