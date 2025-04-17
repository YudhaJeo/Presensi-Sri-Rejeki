plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    // Google services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sri_rejeki_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sri_rejeki_app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Binding
    viewBinding {
        enable = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    val cameraxVersion = "1.3.0"

    // Default
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Implementation (libs.firebase.database)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Camera
    implementation(libs.androidx.camera.view)
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx:20.1.0")

    //Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ML Kit barcode scanner
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // Firebase BoM (biar semua versi Firebase sinkron)
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    // Library untuk Fused Location Provider
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // Material Compose
    implementation("com.google.android.material:material:1.11.0")
}
