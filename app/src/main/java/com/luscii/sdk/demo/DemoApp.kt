package com.luscii.sdk.demo

import android.app.Application
import com.luscii.sdk.Luscii
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class DemoApp : Application() {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: DemoApp

        // Normally passed around using a dependency injection framework.
        val luscii by lazy {
            Luscii {
                applicationContext = instance.applicationContext
                useDynamicColors = true
            }
        }
    }
}