package com.example.bookxpert


import android.app.Application
import com.example.bookxpert.data.local.database.AppDatabase

class AuthApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val myFirebaseMessagingService = MyFirebaseMessagingService() // Instantiate but don't use directly for context
}