plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
//    id("com.android.application")
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.vadym.birthday"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vadym.birthday"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // DAGGER
    implementation (platform("com.google.dagger:dagger:2.35.1"))
    implementation (platform("com.google.dagger:dagger-android:2.35.1"))
    implementation (platform("com.google.dagger:dagger-android-support:2.15"))
    annotationProcessor (platform("com.google.dagger:dagger-compiler:2.24"))

    // GLIDE
//    implementation (com.github.bumptech.glide)
//    implementation 'com.github.bumptech.glide:glide:4.12.0'
}