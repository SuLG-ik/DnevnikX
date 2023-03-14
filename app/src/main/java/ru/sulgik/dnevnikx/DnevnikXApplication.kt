package ru.sulgik.dnevnikx

import android.app.Application
import ru.sulgik.koin.main.setupKoin

class DnevnikXApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        setupNapier()
        setupKoin()
    }

}