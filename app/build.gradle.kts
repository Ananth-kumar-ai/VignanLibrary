plugins {
    alias(libs.plugins.android.application)
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation("com.airbnb.android:lottie:3.7.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.19")
    //load profile
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

}