import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun localProperty(name: String): String =
    providers.gradleProperty(name).orNull ?: localProperties.getProperty(name, "")

android {
    namespace = "com.groceryb2b.core.network"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        // Base URL is injected per build-type so staging/prod can point
        // to different backends without code changes.
        buildConfigField("String", "BASE_URL", "\"https://api.groceryb2b.example.com/\"")
        buildConfigField("String", "SUPABASE_URL", "\"${localProperty("SUPABASE_URL")}\"")
        buildConfigField(
            "String",
            "SUPABASE_PUBLISHABLE_KEY",
            "\"${localProperty("SUPABASE_PUBLISHABLE_KEY")}\""
        )
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
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    api(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
