plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}


ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
android {
    namespace = "ru.sulgik.dnevnikx"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.sulgik.dnevnikx"
        minSdk = 21
        targetSdk = 33
        versionCode = 8
        versionName = "0.4.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "APP_VERSION", "\"v0.4.1\"")
        }
        debug {
            buildConfigField("String", "APP_VERSION", "\"v0.3.1-001\"")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
    productFlavors {
        val dev by creating {
            applicationIdSuffix = ".dev"
        }
        val production by creating {

        }
    }
}

dependencies {
    implementation(projects.ui.core)
    implementation(projects.koin.main)
    implementation(projects.main.component)
    implementation(projects.core.common)
    implementation(projects.core.components)

    implementation(libs.bundles.decompose)
    coreLibraryDesugaring(libs.desugar.libs)
    implementation(libs.bundles.compose)
    implementation(libs.napier)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.android)
    implementation(libs.bundles.koin)
    implementation(libs.activity.core)
    implementation(libs.material)
    implementation(libs.activity.compose)
    debugImplementation(libs.bundles.compose.debug)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.bundles.compose.test)
}