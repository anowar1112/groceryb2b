plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}
android { namespace = "com.groceryb2b.feature.home"; compileSdk = libs.versions.compileSdk.get().toInt(); defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }; buildFeatures { compose = true }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies {
    implementation(project(":core:core-database")); implementation(project(":core:core-network")); implementation(project(":core:core-ui"))
    implementation(platform(libs.compose.bom)); implementation(libs.compose.ui); implementation(libs.compose.material3); implementation(libs.lifecycle.viewmodel.compose); implementation(libs.hilt.navigation.compose); implementation(libs.navigation.compose); implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android); ksp(libs.hilt.compiler)
}
