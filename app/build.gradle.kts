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
    compileSdk = 33
    namespace = "ru.sulgik.dnevnikx"

    defaultConfig {
        applicationId = "ru.sulgik.dnevnikx"
        minSdk = 21
        targetSdk = 33
        versionCode = 10
        versionName = "0.7.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        resourceConfigurations += listOf("ru", "en")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
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
    packaging {
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

    flavorDimensions += "dnevnikx"
    productFlavors {
        create("dev") {
            dimension = "dnevnikx"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("production") {
            dimension = "dnevnikx"
            applicationId = "ru.sulgik.dnevnikx"
        }
    }
}

dependencies {
    implementation(projects.ui.core)
    implementation(projects.koin.main)
    implementation(projects.main.component)
    implementation(projects.core.common)
    implementation(projects.core.components)
    implementation(projects.images.ui)

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