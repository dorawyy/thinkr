package com.example.thinkr.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes the Koin dependency injection framework for Thinkr.
 *
 * This singleton object provides a centralized way to start Koin and register application modules.
 * It encapsulates the initialization logic and ensures consistent setup across the application.
 */
object KoinInitializer {
    /**
     * Initializes Koin with the application modules.
     *
     * Sets up the Koin DI container with all application modules and applies
     * any additional configuration specified by the caller.
     *
     * @param config Optional configuration block that can be used to customize
     * the Koin container setup (e.g., for adding Android context).
     */
    fun initKoin(config: KoinAppDeclaration? = null) {
        startKoin {
            config?.invoke(this)
            modules(appModule)
        }
    }
}
