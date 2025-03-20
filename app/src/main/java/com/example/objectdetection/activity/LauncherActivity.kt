package com.example.objectdetection.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class LauncherActivity : ComponentActivity() {
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
                showScreen(isCompose, isUpdateDialog)
            } else {
                Log.e("LauncherActivity", "Remote Config fetch failed", task.exception)
                showScreen(isCompose = false, isUpdateDialog = false)
            }
        }
    }

    private fun showScreen(isCompose: Boolean, isUpdateDialog: Boolean) {
        val intent = if (isCompose) {
            Intent(this, MainComposeActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        intent.putExtra("isUpdateDialog", isUpdateDialog)
        startActivity(intent)
        finish()
    }
}