package com.praxis.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*

/**
 * Reusable info button that shows an explanatory dialog when tapped.
 * Add to TopAppBar actions or inline header rows on every screen.
 */
@Composable
fun InfoButton(title: String, description: String) {
    var show by remember { mutableStateOf(false) }

    IconButton(onClick = { show = true }) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "About this screen",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            title = { Text(title) },
            text = { Text(description, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = { show = false }) { Text("Got it") }
            }
        )
    }
}
