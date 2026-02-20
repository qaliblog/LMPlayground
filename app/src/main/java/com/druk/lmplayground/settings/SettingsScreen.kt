@file:OptIn(ExperimentalMaterial3Api::class)

package com.druk.lmplayground.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.druk.lmplayground.R
import com.druk.lmplayground.server.LlamaServer
import com.druk.lmplayground.theme.PlaygroundTheme

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onModelsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onFaqClick: () -> Unit,
    appVersion: String,
    isServerEnabled: Boolean = false,
    onServerEnabledChange: (Boolean) -> Unit = {},
    serverUrl: String? = null,
    // Model Settings
    contextLength: String,
    onContextLengthChange: (String) -> Unit,
    batchSize: String,
    onBatchSizeChange: (String) -> Unit,
    threads: String,
    onThreadsChange: (String) -> Unit,
    temperature: String,
    onTemperatureChange: (String) -> Unit,
    topP: String,
    onTopPChange: (String) -> Unit,
    minP: String,
    onMinPChange: (String) -> Unit,
    topK: String,
    onTopKChange: (String) -> Unit,
    repeatPenalty: String,
    onRepeatPenaltyChange: (String) -> Unit,
    stopTokens: String,
    onStopTokensChange: (String) -> Unit,
    // Model Actions
    loadedModelName: String?,
    onLoadClick: () -> Unit,
    onReloadClick: () -> Unit,
    onEjectClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Models row
            SettingsRow(
                icon = Icons.Outlined.Storage,
                title = stringResource(R.string.models),
                subtitle = stringResource(R.string.models_subtitle),
                onClick = onModelsClick
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Server row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Dns,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.local_server),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (isServerEnabled && serverUrl != null)
                            stringResource(R.string.server_running_at, serverUrl)
                            else stringResource(R.string.local_server_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isServerEnabled) {
                        Text(
                            text = stringResource(R.string.api_model_name, LlamaServer.DEFAULT_MODEL_NAME),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isServerEnabled,
                    onCheckedChange = onServerEnabledChange
                )
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Model Settings Header
            Text(
                text = stringResource(R.string.model_settings),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            // Loaded Model Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = loadedModelName ?: stringResource(R.string.no_model_loaded),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (loadedModelName != null) {
                    OutlinedButton(onClick = onEjectClick) {
                        Text(stringResource(R.string.eject_model))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onReloadClick) {
                        Text(stringResource(R.string.reload_model))
                    }
                } else {
                    Button(onClick = onLoadClick) {
                        Text(stringResource(R.string.load_model))
                    }
                }
            }

            // Settings Fields
            ModelSettingField(
                label = stringResource(R.string.context_length),
                value = contextLength,
                onValueChange = onContextLengthChange
            )
            ModelSettingField(
                label = stringResource(R.string.batch_size),
                value = batchSize,
                onValueChange = onBatchSizeChange
            )
            ModelSettingField(
                label = stringResource(R.string.threads),
                value = threads,
                onValueChange = onThreadsChange
            )
            ModelSettingField(
                label = stringResource(R.string.temperature),
                value = temperature,
                onValueChange = onTemperatureChange
            )
            ModelSettingField(
                label = stringResource(R.string.top_p),
                value = topP,
                onValueChange = onTopPChange
            )
            ModelSettingField(
                label = stringResource(R.string.min_p),
                value = minP,
                onValueChange = onMinPChange
            )
            ModelSettingField(
                label = stringResource(R.string.top_k),
                value = topK,
                onValueChange = onTopKChange
            )
            ModelSettingField(
                label = stringResource(R.string.repeat_penalty),
                value = repeatPenalty,
                onValueChange = onRepeatPenaltyChange
            )
            ModelSettingField(
                label = stringResource(R.string.stop_tokens),
                value = stopTokens,
                onValueChange = onStopTokensChange,
                placeholder = stringResource(R.string.stop_tokens_hint)
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Privacy Policy row
            SettingsRow(
                icon = Icons.Outlined.Policy,
                title = stringResource(R.string.privacy_policy),
                onClick = onPrivacyPolicyClick
            )

            // FAQ row
            SettingsRow(
                icon = Icons.Outlined.HelpOutline,
                title = stringResource(R.string.faq),
                subtitle = stringResource(R.string.faq_subtitle),
                onClick = onFaqClick
            )

            // Version row (static, not clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.version, appVersion),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ModelSettingField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        singleLine = true
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PlaygroundTheme {
        SettingsScreen(
            onBackClick = {},
            onModelsClick = {},
            onPrivacyPolicyClick = {},
            onFaqClick = {},
            appVersion = "1.0.0",
            contextLength = "2048",
            onContextLengthChange = {},
            batchSize = "2048",
            onBatchSizeChange = {},
            threads = "4",
            onThreadsChange = {},
            temperature = "0.8",
            onTemperatureChange = {},
            topP = "0.95",
            onTopPChange = {},
            minP = "0.05",
            onMinPChange = {},
            topK = "40",
            onTopKChange = {},
            repeatPenalty = "1.1",
            onRepeatPenaltyChange = {},
            stopTokens = "",
            onStopTokensChange = {},
            loadedModelName = "Llama 3.2 1B",
            onLoadClick = {},
            onReloadClick = {},
            onEjectClick = {}
        )
    }
}
