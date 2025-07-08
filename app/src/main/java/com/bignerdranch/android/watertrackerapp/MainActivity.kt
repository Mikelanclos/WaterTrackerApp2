package com.bignerdranch.android.watertrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bignerdranch.android.watertrackerapp.ui.theme.WaterTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaterTrackerAppTheme {
                WaterTrackerApp()
            }
        }
    }
}
