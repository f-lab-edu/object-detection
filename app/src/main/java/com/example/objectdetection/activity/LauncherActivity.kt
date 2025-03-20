package com.example.objectdetection.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class LauncherActivity : ComponentActivity() {
    companion object {
        const val IS_OBJECT_DETECTION = "isObjectDetection"
        const val IS_UPDATE_DIALOG = "isUpdateDialog"
    }

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val isCompose = remoteConfig.getBoolean("show_compose")
                val isUpdateDialog = remoteConfig.getBoolean("version_update")
                val minOsVersion = remoteConfig.getLong("object_detection_min_os_version")
                showScreen(isCompose, isUpdateDialog, minOsVersion)
            } else {
                Log.e("LauncherActivity", "Remote Config fetch failed", task.exception)
                showScreen(isCompose = false, isUpdateDialog = false, minOsVersion = 0)
            }
        }
    }

    private fun showScreen(isCompose: Boolean, isUpdateDialog: Boolean, minOsVersion: Long) {
        val isObjectDetection = Build.VERSION.SDK_INT >= minOsVersion
        val intent = if (isCompose) {
            Intent(this, MainComposeActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        intent.putExtra(IS_UPDATE_DIALOG, isUpdateDialog)
        intent.putExtra(IS_OBJECT_DETECTION, isObjectDetection)
        startActivity(intent)
        finish()
    }
}