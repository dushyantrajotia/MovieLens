import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

val defaultWebClientId: String =
    (project.findProperty("DEFAULT_WEB_CLIENT_ID") ?: "DUMMY_ID").toString()

// Read from local.properties - FIXED VERSION
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    println("DEBUG: local.properties file exists")
    localPropertiesFile.inputStream().use { localProperties.load(it) }
} else {
    println("DEBUG: local.properties file NOT FOUND")
}
val tmdbApiKey = localProperties.getProperty("tmdb_api_key", "")

println("DEBUG: TMDB API Key length: ${tmdbApiKey.length}")
println("DEBUG: TMDB API Key: ${if (tmdbApiKey.isNotEmpty()) "***${tmdbApiKey.takeLast(4)}" else "EMPTY"}")

android {
    namespace = "com.devfusion.movielens"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.devfusion.movielens"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

//        resValue("string", "default_web_client_id", "\"$defaultWebClientId\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    // Use a compose compiler extension that matches your Compose version
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Firebase (BOM manages versions)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx") // use ktx for Kotlin
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Google sign-in
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1") // or latest
    implementation("androidx.activity:activity-ktx:1.7.2")
    // add this (version should match your lifecycle libs; 2.6.1 works)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Networking & JSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines (for ViewModel)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // XML / AppCompat (for your Activities)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}