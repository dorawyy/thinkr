package com.example.thinkr

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class MockKTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        io.mockk.MockKAnnotations.init(this, relaxUnitFun = true)
        return super.newApplication(cl, name, context)
    }
}
