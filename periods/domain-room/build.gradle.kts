plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "ru.sulgik.periods.domain.room"
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
    api(projects.periods.domain)
    implementation(projects.account.domainRoom)
    implementation(projects.core.common)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.android)
    implementation(libs.koin.core)
    testImplementation(libs.junit)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.room.testing)
}