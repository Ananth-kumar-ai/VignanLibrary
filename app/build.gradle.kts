plugins {
    alias(libs.plugins.android.application)
    // FIX 1: Added the Kotlin plugin for Android

}

android {
    namespace = "org.vignanuniversity.vignanlibrary"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.vignanuniversity.vignanlibrary"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // FIX 3: This block is now correct because the kotlin-android plugin is active

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // FIX 4: Updated Lottie to a recent stable version
    implementation("com.airbnb.android:lottie:6.4.1")
    implementation("com.android.volley:volley:1.2.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // GIF support
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")

    // FIX 6: Updated Security-Crypto to the stable version
    implementation("androidx.security:security-crypto:1.0.0")

    // FIX 7: Updated Core-KTX to a recent stable version
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.security:security-crypto:1.0.0")




    // Add this line for encryption tools like MasterKey 🔐
    implementation("androidx.security:security-crypto:1.0.0")

    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}