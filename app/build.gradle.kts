plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt") // <- this is correct for Kotlin 2.x
}

android {
    namespace = "erik.strinnholm.rpg_inventory_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "erik.strinnholm.rpg_inventory_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { viewBinding = true }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}

dependencies {
    implementation(libs.androidx.room.runtime)              //Room database
    implementation(libs.androidx.room.ktx)                  //Room database
    kapt(libs.androidx.room.compiler)                       //Room database
    implementation(libs.androidx.lifecycle.viewmodel.ktx)   //viewmodel
    implementation(libs.androidx.lifecycle.livedata.ktx)    //viewmodel
    implementation(libs.androidx.fragment.ktx)              //fragments
    implementation(libs.kotlinx.coroutines.core)            //Coroutines
    implementation(libs.kotlinx.coroutines.android)         //Coroutines

    //default android studio libraries
    implementation(libs.androidx.core.ktx)                  //Default
    implementation(libs.androidx.appcompat)                 //Default
    implementation(libs.material)                           //Default
    testImplementation(libs.junit)                          //testing
    androidTestImplementation(libs.androidx.junit)          //testing
    androidTestImplementation(libs.androidx.espresso.core)  //testing?
}