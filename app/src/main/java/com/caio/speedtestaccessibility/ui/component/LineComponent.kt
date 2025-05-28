package com.caio.speedtestaccessibility.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.caio.speedtestaccessibility.ui.theme.SpeedTestAccessibilityTheme

@Composable
fun LineComponent(title: String, message: String) {
    Row {
        Text(
            text = "$title: $message",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LineComponentPreview() {
    SpeedTestAccessibilityTheme {
        LineComponent("Download", "104Mbps")
    }
}