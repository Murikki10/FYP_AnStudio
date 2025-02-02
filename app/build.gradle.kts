plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fyp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fyp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.activity)
    implementation (libs.jbcrypt)
    implementation (libs.material.v140)
    implementation (libs.logging.interceptor)
    implementation (libs.okhttp)
    implementation (libs.material.v190)
    implementation (libs.logging.interceptor.v491)
    implementation (libs.gson)
    implementation (libs.material.v150)

    // AndroidX
    implementation (libs.appcompat.v161)
    implementation (libs.constraintlayout.v214)

    implementation (libs.circleimageview)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
}