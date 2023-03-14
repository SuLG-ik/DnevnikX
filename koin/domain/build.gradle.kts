plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.sulgik.koin.domain"
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
    implementation(projects.auth.domainKtor)
    implementation(projects.auth.domainRoom)

    implementation(projects.diary.domainRoom)
    implementation(projects.diary.domainKtor)
    implementation(projects.diary.domainMerged)

    implementation(projects.periods.domainRoom)
    implementation(projects.periods.domainKtor)
    implementation(projects.periods.domainMerged)

    implementation(projects.account.domainRoom)
    implementation(projects.account.domainKtor)
    implementation(projects.account.domainDatastore)

    implementation(projects.marks.domainRoom)
    implementation(projects.marks.domainKtor)
    implementation(projects.marks.domainMerged)

    implementation(projects.finalmarks.domainRoom)
    implementation(projects.finalmarks.domainKtor)
    implementation(projects.finalmarks.domainMerged)

    implementation(projects.schedule.domainKtor)

    implementation(projects.about.domainBuiltin)

    implementation(libs.koin.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
}