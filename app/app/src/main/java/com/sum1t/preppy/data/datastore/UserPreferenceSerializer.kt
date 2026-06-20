package com.sum1t.preppy.data.datastore

import androidx.datastore.core.Serializer
import com.sum1t.preppy.datastore.UserPreferences
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {

//    override val defaultValue: UserPreferences =
//        UserPreferences.getDefaultInstance()

    override val defaultValue: UserPreferences =
        UserPreferences.newBuilder()
            .setHapticsEnabled(true)
            .setNotificationsEnabled(true)
            .build()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            UserPreferences.parseFrom(input)
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: UserPreferences,
        output: OutputStream
    ) {
        t.writeTo(output)
    }
}
