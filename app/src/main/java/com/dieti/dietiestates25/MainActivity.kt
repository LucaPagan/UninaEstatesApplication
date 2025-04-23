package com.dieti.dietiestates25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dieti.dietiestates25.ui.screen.PropertyDetailScreen
import com.dieti.dietiestates25.ui.screen.WelcomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeScreen()
        }
    }
}