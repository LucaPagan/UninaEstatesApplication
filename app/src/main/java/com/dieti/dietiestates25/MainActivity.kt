package com.dieti.dietiestates25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.dieti.dietiestates25.ui.navigation.Navigation
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DietiEstatesTheme {
                Navigation()
            }
        }
    }
}