plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.sulgik.marks.mvi.impl"
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
        isCoreLibraryDesugaringEnabled = true
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
    coreLibraryDesugaring(libs.desugar.libs)
    implementation(projects.core.common)
    implementation(projects.auth.core)
    implementation(projects.marks.domain)
    implementation(projects.periods.domain)
    implementation(projects.marks.mvi)
    implementation(projects.core.components)
    implementation(projects.settings.provider)
    implementation(projects.diary.settings)

    implementation(libs.bundles.module.mvi)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.room.testing)
}