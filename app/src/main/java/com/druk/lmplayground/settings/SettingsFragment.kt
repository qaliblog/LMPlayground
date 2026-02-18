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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.druk.lmplayground.BuildConfig
import com.druk.lmplayground.R
import com.druk.lmplayground.server.LlamaServerService
import com.druk.lmplayground.storage.StoragePreferences
import com.druk.lmplayground.theme.PlaygroundTheme
import com.druk.lmplayground.util.NetworkUtils

class SettingsFragment : Fragment() {

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
                        if (enabled) {
                            LlamaServerService.start(context)
                        } else {
                            LlamaServerService.stop(context)
                        }
                    },
                    serverUrl = serverUrl
                )
            }
        }
    }
}
