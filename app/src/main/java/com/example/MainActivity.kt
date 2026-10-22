package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.StudentDashboardScreen
import com.example.ui.StudentDashboardViewModel
import com.example.ui.theme.SHULEETheme

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: StudentDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SHULEETheme {
                StudentDashboardScreen(viewModel = dashboardViewModel)
            }
        }
    }
}
