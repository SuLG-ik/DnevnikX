plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.sulgik.marksupdates.ui"
    compileSdk = 33
    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlin {
        jvmToolchain(11)
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(projects.marksupdates.mvi)
    implementation(projects.core.common)
    implementation(projects.ui.core)
    implementation(projects.periods.ui)
    implementation(libs.bundles.compose)
    implementation(libs.paging.compose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.android)
    testImplementation(libs.junit)
    implementation(libs.kotlinx.collections)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.room.testing)
}