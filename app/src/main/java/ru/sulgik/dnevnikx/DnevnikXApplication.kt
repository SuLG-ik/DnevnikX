package ru.sulgik.dnevnikx

import android.app.Application

class DnevnikXApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        setupNapier()
        setupDI()
    }

}