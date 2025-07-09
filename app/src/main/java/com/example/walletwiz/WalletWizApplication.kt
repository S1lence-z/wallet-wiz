package com.example.walletwiz

import android.app.Application
import com.example.walletwiz.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WalletWizApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)  //! Use Level.INFO or Level.ERROR in production
            androidContext(this@WalletWizApplication)
            modules(appModules)
        }
    }
}
