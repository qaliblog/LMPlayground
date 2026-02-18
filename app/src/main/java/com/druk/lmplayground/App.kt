package com.druk.lmplayground

import android.app.Application
import com.druk.llamacpp.LlamaCpp
import com.druk.llamacpp.LlamaModel
import com.druk.lmplayground.models.ModelInfo

class App: Application() {

    val llamaCpp = LlamaCpp()
    var currentModel: LlamaModel? = null
    var currentModelInfo: ModelInfo? = null

    override fun onCreate() {
        super.onCreate()
        llamaCpp.init()
    }
}