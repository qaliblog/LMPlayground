package com.druk.lmplayground

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.core.content.ContextCompat
import com.druk.lmplayground.server.LlamaServerService
import com.druk.lmplayground.storage.StoragePreferences
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.druk.lmplayground.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {

    private var folderPickerCallback: ((Uri?) -> Unit)? = null

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            LlamaServerService.start(this)
        }
    }

    val folderPickerLauncher: ActivityResultLauncher<Uri?> = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        folderPickerCallback?.invoke(uri)
        folderPickerCallback = null
    }

    fun launchFolderPicker(callback: (Uri?) -> Unit) {
        folderPickerCallback = callback
        folderPickerLauncher.launch(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val prefs = StoragePreferences(this)
        if (prefs.isServerEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    LlamaServerService.start(this)
                }
            } else {
                LlamaServerService.start(this)
            }
        }

        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    AndroidViewBinding(ContentMainBinding::inflate)
                }
            }
        )
    }
}
