import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("kapt") version "1.9.0"
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
}
buildscript {

    val kotlin_version by extra("1.9.0")
    val google_services by extra("4.4.1")
    val gradle by extra("8.3.1")
    //        val okhttpVersion = "3.10.0"


    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:$gradle")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath ("com.google.gms:google-services:$google_services")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    tasks.withType<KaptGenerateStubsTask> {
        kotlinOptions.jvmTarget = "1.8"
    }

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}