package com.example.summerholidays

import android.app.Application
import com.example.summerholidays.database.ImageDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ImageDatabase.create(this)
    }
}