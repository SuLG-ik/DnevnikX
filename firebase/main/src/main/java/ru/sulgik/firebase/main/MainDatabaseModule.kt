package ru.sulgik.firebase.main

import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.koin.dsl.bind
import org.koin.dsl.module


class FirebaseModule {

    val module = module {
        single { Firebase.firestore } bind FirebaseFirestore::class
        single { Firebase.analytics } bind FirebaseAnalytics::class
        single { Firebase.crashlytics } bind FirebaseCrashlytics::class
        single { Firebase.storage } bind FirebaseStorage::class
        single { Firebase.app } bind FirebaseApp::class
    }

}