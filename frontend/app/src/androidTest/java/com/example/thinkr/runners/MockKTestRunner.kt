package com.example.thinkr.runners

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Custom test runner for instrumented tests that utilizes MockK.
 *
 * Extends the standard AndroidJUnitRunner and initializes MockK annotations
 * before the application is created, allowing for mocked dependencies in tests.
 */
class MockKTestRunner : AndroidJUnitRunner() {
    /**
     * Initializes MockK annotations before creating the application instance.
     *
     * @param cl The ClassLoader to use for creating the application
     * @param name The name of the application class to instantiate
     * @param context The context to use for creating the application
     * @return The created application instance
     */
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        io.mockk.MockKAnnotations.init(this, relaxUnitFun = true)
        return super.newApplication(cl, name, context)
    }
}
