package com.example.objectdetection.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.objectdetection.ui.component.UpdateDialog
import com.example.objectdetection.ui.screen.DetailScreen
import com.example.objectdetection.ui.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isUpdateDialog = intent.getBooleanExtra("isUpdateDialog", false)

        setContent {
            val navController = rememberNavController()
            var showDialog by rememberSaveable { mutableStateOf(isUpdateDialog) }

            if (showDialog) {
                UpdateDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { showDialog = false }
                )
            }

            NavHost(navController = navController, startDestination = "main_screen") {
                composable("main_screen") { MainScreen(navController) }
                composable("detail_screen") { DetailScreen(navController) }
            }
        }
    }
}