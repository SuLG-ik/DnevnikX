plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.sulgik.koin.mvi"
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
}

dependencies {
    implementation(projects.diary.mviImpl)
    implementation(projects.about.mviImpl)
    implementation(projects.marks.mviImpl)
    implementation(projects.marksedit.mviImpl)
    implementation(projects.finalmarks.mviImpl)
    implementation(projects.schedule.list.mviImpl)
    implementation(projects.auth.mviImpl)
    implementation(projects.account.mviImpl)
    implementation(projects.accountSelector.mviImpl)
    implementation(projects.main.mviImpl)
    implementation(projects.application.mviImpl)
    implementation(projects.experimentalsettings.mviImpl)
    implementation(projects.marksupdates.mviImpl)
    implementation(projects.schedule.add.mviImpl)

    implementation(libs.koin.core)
    implementation(libs.bundles.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
}