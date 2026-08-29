plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.groceryb2b.core.network"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        // Base URL is injected per build-type so staging/prod can point
        // to different backends without code changes.
        buildConfigField("String", "BASE_URL", "\"https://api.groceryb2b.example.com/\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
