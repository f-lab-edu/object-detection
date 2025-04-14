package com.example.objectdetection.repository

import com.example.objectdetection.data.RemoteConfigData
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteConfigRepository @Inject constructor() {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    suspend fun fetchRemoteConfig(): Result<RemoteConfigData> {
        return try {
            remoteConfig.fetchAndActivate().await()

            val isCompose = remoteConfig.getBoolean("show_compose")
            val isUpdateDialog = remoteConfig.getBoolean("version_update")
            val minOsVersion = remoteConfig.getLong("object_detection_min_os_version")

            Result.success(
                RemoteConfigData(
                    isCompose = isCompose,
                    isUpdateDialog = isUpdateDialog,
                    minOsVersion = minOsVersion
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
