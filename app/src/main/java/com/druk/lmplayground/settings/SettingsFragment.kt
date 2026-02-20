package com.druk.lmplayground.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.druk.lmplayground.BuildConfig
import com.druk.lmplayground.R
import com.druk.lmplayground.server.LlamaServerService
import com.druk.lmplayground.conversation.ConversationViewModel
import com.druk.lmplayground.storage.StoragePreferences
import com.druk.lmplayground.theme.PlaygroundTheme
import com.druk.lmplayground.util.NetworkUtils

class SettingsFragment : Fragment() {

    private val conversationViewModel: ConversationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val prefs = StoragePreferences(inflater.context)
        setContent {
            PlaygroundTheme {
                var isServerEnabled by remember { mutableStateOf(prefs.isServerEnabled) }
                val loadedModel by conversationViewModel.loadedModel.observeAsState()

                var contextLength by remember { mutableStateOf(prefs.contextLength.toString()) }
                var batchSize by remember { mutableStateOf(prefs.batchSize.toString()) }
                var threads by remember { mutableStateOf(prefs.threads.toString()) }
                var temperature by remember { mutableStateOf(prefs.temperature.toString()) }
                var topP by remember { mutableStateOf(prefs.topP.toString()) }
                var minP by remember { mutableStateOf(prefs.minP.toString()) }
                var topK by remember { mutableStateOf(prefs.topK.toString()) }
                var repeatPenalty by remember { mutableStateOf(prefs.repeatPenalty.toString()) }
                var stopTokens by remember { mutableStateOf(prefs.stopTokens) }

                val ipAddress = NetworkUtils.getLocalIpAddress() ?: "localhost"
                val serverUrl = "http://$ipAddress:8080/v1"

                SettingsScreen(
                    onBackClick = { findNavController().popBackStack() },
                    onModelsClick = {
                        findNavController().navigate(R.id.action_settings_to_models)
                    },
                    onPrivacyPolicyClick = {
                        findNavController().navigate(R.id.action_settings_to_privacy_policy)
                    },
                    onFaqClick = {
                        findNavController().navigate(R.id.action_settings_to_faq)
                    },
                    appVersion = BuildConfig.VERSION_NAME,
                    isServerEnabled = isServerEnabled,
                    onServerEnabledChange = { enabled ->
                        isServerEnabled = enabled
                        prefs.isServerEnabled = enabled
                        LlamaServerService.updateStatus(context)
                    },
                    serverUrl = serverUrl,
                    contextLength = contextLength,
                    onContextLengthChange = { contextLength = it; prefs.contextLength = it.toIntOrNull() ?: 2048 },
                    batchSize = batchSize,
                    onBatchSizeChange = { batchSize = it; prefs.batchSize = it.toIntOrNull() ?: 2048 },
                    threads = threads,
                    onThreadsChange = { threads = it; prefs.threads = it.toIntOrNull() ?: 4 },
                    temperature = temperature,
                    onTemperatureChange = { temperature = it; prefs.temperature = it.toFloatOrNull() ?: 0.8f },
                    topP = topP,
                    onTopPChange = { topP = it; prefs.topP = it.toFloatOrNull() ?: 0.95f },
                    minP = minP,
                    onMinPChange = { minP = it; prefs.minP = it.toFloatOrNull() ?: 0.05f },
                    topK = topK,
                    onTopKChange = { topK = it; prefs.topK = it.toIntOrNull() ?: 40 },
                    repeatPenalty = repeatPenalty,
                    onRepeatPenaltyChange = { repeatPenalty = it; prefs.repeatPenalty = it.toFloatOrNull() ?: 1.1f },
                    stopTokens = stopTokens,
                    onStopTokensChange = { stopTokens = it; prefs.stopTokens = it },
                    loadedModelName = loadedModel?.name,
                    onLoadClick = {
                        findNavController().navigate(R.id.action_settings_to_models)
                    },
                    onReloadClick = {
                        loadedModel?.let { conversationViewModel.loadModel(it) }
                    },
                    onEjectClick = {
                        conversationViewModel.unloadModel()
                    }
                )
            }
        }
    }
}
