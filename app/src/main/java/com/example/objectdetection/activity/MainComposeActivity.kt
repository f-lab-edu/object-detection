package com.example.objectdetection.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class MainComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeScreen()
        }
    }
}

@Composable
fun ComposeScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("This is the Compose Activity")
    }
}