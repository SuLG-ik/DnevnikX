plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.sulgik.koin.main"
    compileSdk = 33
    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    implementation(projects.koin.mvi)
    implementation(projects.koin.domain)
    implementation(projects.koin.settings)

    implementation(projects.settings.providerDatastore)
    implementation(projects.ktor.main)
    implementation(projects.auth.ktor)
    implementation(projects.mvi.main)
    implementation(projects.room.main)
    implementation(projects.room.auth)
    implementation(projects.core.common)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.bundles.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
}