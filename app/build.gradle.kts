plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.zcard.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zcard.android"
        minSdk = 26
        targetSdk = 36
        versionCode = project.findProperty("versionCode")?.toString()?.toInt() ?: 4

        val baseVersion = "1.7.0"
        versionName = project.findProperty("versionName")?.toString() ?: baseVersion

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(project(":feature"))
    implementation(project(":core:data"))
    implementation(project(":core:analytics"))

    // Hilt
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)

    // Firebase
    implementation(platform(libs.google.firebase.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}