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
        private const val KEY_CONTEXT_LENGTH = "context_length"
        private const val KEY_BATCH_SIZE = "batch_size"
        private const val KEY_THREADS = "threads"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_TOP_P = "top_p"
        private const val KEY_MIN_P = "min_p"
        private const val KEY_TOP_K = "top_k"
        private const val KEY_STOP_TOKENS = "stop_tokens"
        private const val KEY_REPEAT_PENALTY = "repeat_penalty"
    }

    var modelStorageUri: Uri?
        get() = prefs.getString(KEY_URI, null)?.toUri()
        set(value) = prefs.edit { putString(KEY_URI, value?.toString()) }

    var isServerEnabled: Boolean
        get() = prefs.getBoolean(KEY_SERVER_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_SERVER_ENABLED, value) }

    var contextLength: Int
        get() = prefs.getInt(KEY_CONTEXT_LENGTH, 2048)
        set(value) = prefs.edit { putInt(KEY_CONTEXT_LENGTH, value) }

    var batchSize: Int
        get() = prefs.getInt(KEY_BATCH_SIZE, 2048)
        set(value) = prefs.edit { putInt(KEY_BATCH_SIZE, value) }

    var threads: Int
        get() = prefs.getInt(KEY_THREADS, (Runtime.getRuntime().availableProcessors() - 2).coerceAtLeast(1))
        set(value) = prefs.edit { putInt(KEY_THREADS, value) }

    var temperature: Float
        get() = prefs.getFloat(KEY_TEMPERATURE, 0.8f)
        set(value) = prefs.edit { putFloat(KEY_TEMPERATURE, value) }

    var topP: Float
        get() = prefs.getFloat(KEY_TOP_P, 0.95f)
        set(value) = prefs.edit { putFloat(KEY_TOP_P, value) }

    var minP: Float
        get() = prefs.getFloat(KEY_MIN_P, 0.05f)
        set(value) = prefs.edit { putFloat(KEY_MIN_P, value) }

    var topK: Int
        get() = prefs.getInt(KEY_TOP_K, 40)
        set(value) = prefs.edit { putInt(KEY_TOP_K, value) }

    var repeatPenalty: Float
        get() = prefs.getFloat(KEY_REPEAT_PENALTY, 1.1f)
        set(value) = prefs.edit { putFloat(KEY_REPEAT_PENALTY, value) }

    var stopTokens: String
        get() = prefs.getString(KEY_STOP_TOKENS, "") ?: ""
        set(value) = prefs.edit { putString(KEY_STOP_TOKENS, value) }

    fun clear() {
        prefs.edit { clear() }
    }
}
