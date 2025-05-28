package com.caio.speedtestaccessibility

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import com.caio.speedtestaccessibility.constants.TestState
import com.caio.speedtestaccessibility.model.SpeedTestResult

object SharedState {
    var testState = TestState.IDLE
    var testResult = mutableStateOf(SpeedTestResult())
    var mediaProjectionPermission: ActivityResultLauncher<Intent>? = null
}