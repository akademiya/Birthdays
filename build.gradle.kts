// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.gms.google-services") version "4.4.1" apply false
//    alias(libs.plugins.androidApplication) apply false
//    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
buildscript {
//    ext.kotlin_version = "1.6.21"
//    ext.okhttpVersion="3.10.0"
//    ext.retrofit_version = "2.4.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
//        classpath 'com.android.tools.build:gradle:8.2.2';
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version";
//        classpath 'com.google.gms:google-services:4.4.1';

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
//        google()
//        mavenCentral()
    }
}
