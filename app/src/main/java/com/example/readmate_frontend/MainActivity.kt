package com.example.readmate_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.readmate_frontend.ui.navigate.NavGraph
import com.example.readmate_frontend.ui.theme.ReadMate_FrontendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadMate_FrontendTheme {
                NavGraph()
            }
        }
    }
}
