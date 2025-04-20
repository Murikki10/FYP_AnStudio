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
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.activity)
    implementation (libs.jbcrypt)
    implementation (libs.logging.interceptor)
    implementation (libs.okhttp)
    implementation (libs.material.v190)
    implementation (libs.logging.interceptor.v491)
    implementation (libs.gson)

    // AndroidX
    implementation (libs.appcompat.v161)
    implementation (libs.constraintlayout.v214)

    implementation (libs.circleimageview)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)


    // CameraX
    implementation("androidx.camera:camera-core:1.4.0-alpha02")
    implementation ("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation ("androidx.camera:camera-view:1.4.0-alpha02")

    // Guava
    implementation ("com.google.guava:guava:31.1-android")

    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("jp.wasabeef:richeditor-android:2.0.0")
    implementation ("androidx.cardview:cardview:1.0.0")

    //QR Code
    implementation ("com.google.zxing:core:3.5.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
}