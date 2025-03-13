package com.example.objectdetection.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.objectdetection.ui.screen.DetailScreen
import com.example.objectdetection.ui.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main_screen") {
                composable("main_screen") {
                    MainScreen(navController)
                }
                composable("detail_screen/{photoUrl}/{photoName}") { backStackEntry ->
                    val encodeUrl = backStackEntry.arguments?.getString("photoUrl")
                    val photoName = backStackEntry.arguments?.getString("photoName")

                    encodeUrl?.let { url ->
                        photoName?.let { name ->
                            val decodedUrl = Uri.decode(url)
                            DetailScreen(decodedUrl, name)
                        }
                    }
                }
            }
        }
    }
}