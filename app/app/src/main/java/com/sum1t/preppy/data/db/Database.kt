package com.sum1t.preppy.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.sum1t.data.sqldelight.AppDatabase
import com.sum1t.data.sqldelight.Options
import com.sum1t.data.sqldelight.Questions

object DatabaseFactory {
    fun create(context: Context): AppDatabase {
        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = "preppy.db"
        )

        return AppDatabase(
            driver = driver
        )
    }
}
