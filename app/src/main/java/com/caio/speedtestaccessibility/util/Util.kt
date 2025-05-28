package com.caio.speedtestaccessibility.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import com.caio.speedtestaccessibility.SharedState.mediaProjectionPermission

object Util {
    fun checkAllFilesAccess(activity: Activity): Boolean {
        if (!Environment.isExternalStorageManager()) {
            val packageName = MyApplication.instance.packageName

            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            activity.startActivity(intent)
            return false
        }
        return true
    }

    fun requestScreenshotPermission(context: Context) {
        val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        mediaProjectionPermission?.launch(intent)
    }
}