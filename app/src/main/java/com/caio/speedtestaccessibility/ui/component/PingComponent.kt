package com.caio.speedtestaccessibility.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.caio.speedtestaccessibility.model.PingData
import com.caio.speedtestaccessibility.ui.theme.SpeedTestAccessibilityTheme

@Composable
fun ColumnComponent(title: String, data: PingData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = data.value.toInt().toString(),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Low: ${data.low.toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "High: ${data.high.toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Jitter: ${data.jitter.toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ColumnComponentPreview() {
    val pingData = PingData(795.0, 260.0, 1222.0, 91.0)
    SpeedTestAccessibilityTheme {
        ColumnComponent("Download", pingData)
    }
}