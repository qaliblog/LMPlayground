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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.druk.lmplayground.R
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
    serverUrl: String? = null
) {
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
                }
                Switch(
                    checked = isServerEnabled,
                    onCheckedChange = onServerEnabledChange
                )
            }

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
            appVersion = "1.0.0"
        )
    }
}
