plugins {
    kotlin("kapt") version "1.9.0"
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
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
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":data"))
    implementation(project(":domain"))

    // View Model Life Cycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // DAGGER
//    implementation ("com.google.dagger:dagger:2.35.1")
//    implementation ("com.google.dagger:dagger-android:2.35.1")
//    implementation ("com.google.dagger:dagger-android-support:2.15")
//    implementation("javax.inject:javax.inject:1")
//    ksp("androidx.room:room-compiler:2.5.2")
//    ksp("com.google.dagger:dagger-compiler:2.15")
//    ksp("com.google.dagger:dagger-android-processor:2.24")




    // GLIDE
    implementation ("com.github.bumptech.glide:glide:4.12.0")
}