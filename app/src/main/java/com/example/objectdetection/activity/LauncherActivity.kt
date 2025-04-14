package com.example.objectdetection.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.objectdetection.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {
    companion object {
        const val IS_OBJECT_DETECTION = "isObjectDetection"
        const val IS_UPDATE_DIALOG = "isUpdateDialog"
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchRemoteConfig()

        viewModel.remoteConfigState.observe(this) { result ->
            result?.onSuccess {
                showScreen(it.isCompose, it.isUpdateDialog, it.minOsVersion)
            }?.onFailure {
                Log.e("LauncherActivity", "Remote Config fetch failed", it)
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