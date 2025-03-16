package com.example.thinkr.app

import android.app.Application
import com.example.thinkr.di.KoinInitializer.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Main application class for the Thinkr app.
 *
 * It extends the Android Application class to a provide global application context
 * and perform initialization tasks at application startup.
 */
class Application : Application() {
    /**
     * Called when the application is first created.
     *
     * Initializes the Koin dependency injection framework and provides
     * the application context to it, enabling dependency injection
     * throughout the application.
     */
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@Application)
        }
    }
}
