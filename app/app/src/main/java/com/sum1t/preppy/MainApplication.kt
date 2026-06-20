package com.sum1t.preppy

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sum1t.preppy.common.utils.createNotificationChannel
import com.sum1t.preppy.di.appModules
import com.sum1t.preppy.domain.usecase.ScheduleNotificationsUseCase
import com.sum1t.preppy.domain.usecase.SyncNotificationStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class MainApplication : Application() {
    private val syncNotificationStateUseCase: SyncNotificationStateUseCase by inject()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModules)
        }

        createNotificationChannel(this)

        observeNotificationState()

//        sendTestNotification()

//        Pluto.Installer(this)
//            .addPlugin(PlutoExceptionsPlugin())
//            .addPlugin(PlutoNetworkPlugin())
//            .addPlugin(PlutoLoggerPlugin())
//            .addPlugin(PlutoRoomsDatabasePlugin())
//            .addPlugin(PlutoSharePreferencesPlugin())
//            .install()
//
//        Pluto.showNotch(true)
    }

    private fun observeNotificationState() {
        CoroutineScope(Dispatchers.Default).launch {
            syncNotificationStateUseCase.observe(this@MainApplication)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

    }
}