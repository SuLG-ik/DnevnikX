plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "ru.sulgik.application.component"
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.libs)
    implementation(projects.auth.core)
    implementation(projects.picker.component)
    implementation(projects.modal.component)
    implementation(projects.modal.ui)
    implementation(projects.core.common)
    implementation(projects.core.components)
    implementation(projects.application.ui)
    implementation(projects.ui.component)
    implementation(projects.diary.component)
    implementation(projects.marks.component)
    implementation(projects.accountHost.component)
    implementation(projects.accountSelector.component)
    implementation(projects.application.mvi)

    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.android)
    implementation(libs.bundles.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.room.testing)
}