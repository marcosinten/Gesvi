plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace  = "com.gestionviajes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gestionviajes"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0.0"

        // Custom test runner required by Hilt instrumented tests
        testInstrumentationRunner = "com.gestionviajes.HiltTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable        = true
            isMinifyEnabled     = false
        }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose      = true
        buildConfig  = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

ksp {
    // Keep this relative: absolute Windows paths with spaces break KSP arguments.
    arg("room.schemaLocation", "app/schemas")
}

dependencies {
    // ── Kotlin & Coroutines ───────────────────────────────────────────────────
    implementation(libs.bundles.coroutines)
    implementation(libs.kotlinx.serialization.json)

    // ── Android Core ──────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle)

    // ── Compose (versiones gestionadas por BOM) ───────────────────────────────
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.bundles.compose)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // ── Navigation ────────────────────────────────────────────────────────────
    implementation(libs.navigation.compose)

    // ── Hilt ──────────────────────────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // ── Room ──────────────────────────────────────────────────────────────────
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // ── Tests unitarios ───────────────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // ── Tests instrumentados ──────────────────────────────────────────────────
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.room.testing)
}
