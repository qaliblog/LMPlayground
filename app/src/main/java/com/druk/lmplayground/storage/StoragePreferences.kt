package com.druk.lmplayground.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri

class StoragePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("storage_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_URI = "model_storage_uri"
        private const val KEY_SERVER_ENABLED = "server_enabled"
    }

    var modelStorageUri: Uri?
        get() = prefs.getString(KEY_URI, null)?.toUri()
        set(value) = prefs.edit { putString(KEY_URI, value?.toString()) }

    var isServerEnabled: Boolean
        get() = prefs.getBoolean(KEY_SERVER_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_SERVER_ENABLED, value) }

    fun clear() {
        prefs.edit { clear() }
    }
}
