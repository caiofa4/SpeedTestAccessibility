package com.caio.speedtestaccessibility

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caio.speedtestaccessibility.SharedState.mediaProjectionPermission
import com.caio.speedtestaccessibility.SharedState.testResult
import com.caio.speedtestaccessibility.SharedState.testState
import com.caio.speedtestaccessibility.util.Util.checkAllFilesAccess
import com.caio.speedtestaccessibility.constants.AppPackageNames
import com.caio.speedtestaccessibility.constants.TestState
import com.caio.speedtestaccessibility.model.SpeedTestResult
import com.caio.speedtestaccessibility.service.ScreenshotService
import com.caio.speedtestaccessibility.ui.component.ColumnComponent
import com.caio.speedtestaccessibility.ui.component.LineComponent
import com.caio.speedtestaccessibility.ui.theme.SpeedTestAccessibilityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeedTestAccessibilityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        mediaProjectionPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val serviceIntent = Intent(this, ScreenshotService::class.java).apply {
                    putExtra("resultCode", result.resultCode)
                    putExtra("data", data)
                }
                startForegroundService(serviceIntent)
                openSpeedTestApp(this)
                return@registerForActivityResult
            }
            testState = TestState.FINISHED
        }
    }

    override fun onResume() {
        super.onResume()
        checkAllFilesAccess(this)
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Enable Accessibility Permission")
        }

        Button(
            onClick = {
                testState = TestState.IDLE
                testResult.value = SpeedTestResult()
                openSpeedTestApp(context)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Open SpeedTest")
        }

        val download = "${testResult.value.download}${testResult.value.downloadMetric}"
        val upload = "${testResult.value.upload}${testResult.value.uploadMetric}"
        LineComponent("Download", download)
        LineComponent("Upload", upload)

        Spacer(modifier = Modifier.height(15.dp))
        Text("Ping ${testResult.value.responsiveness.ping.metric}")
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            ColumnComponent("Idle", testResult.value.responsiveness.ping.idle)
            Spacer(modifier = Modifier.width(50.dp))
            ColumnComponent("Download", testResult.value.responsiveness.ping.download)
            Spacer(modifier = Modifier.width(50.dp))
            ColumnComponent("Upload", testResult.value.responsiveness.ping.upload)
        }

        Spacer(modifier = Modifier.height(15.dp))
        Text("Packet Loss%")
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = testResult.value.responsiveness.packetLoss.toString(),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(15.dp))
        Text("Connections")
        Spacer(modifier = Modifier.height(5.dp))
        LineComponent("Connection Type", testResult.value.responsiveness.connections.type)
        LineComponent("Device", testResult.value.responsiveness.connections.device)
    }
}

fun openSpeedTestApp(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setPackage(AppPackageNames.ookla_speed_test)
    val launchIntent = context.packageManager.getLaunchIntentForPackage(AppPackageNames.ookla_speed_test)

    if (launchIntent != null) {
        context.startActivity(launchIntent)
        return
    }

    // App is not installed, open in Play Store
    val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://play.google.com/store/apps/details?id=${AppPackageNames.ookla_speed_test}")
        setPackage("com.android.vending")
    }
    context.startActivity(playStoreIntent)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpeedTestAccessibilityTheme {
        MainScreen()
    }
}