plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.sulgik.core.common"
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
    implementation(libs.android.core)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.essenty)
    implementation(libs.bundles.mvi)

    coreLibraryDesugaring(libs.desugar.libs)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.core)
    implementation(libs.activity.core)
    implementation(libs.ktor.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.datastore)
    implementation(libs.bundles.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
}