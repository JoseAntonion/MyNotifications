package com.example.mynotifications

import android.app.Application
import android.content.Intent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        restartNotificationService()
    }

    private fun restartNotificationService() {
        stopService(Intent(this, NLService::class.java))
        startService(Intent(this, NLService::class.java))
    }

}