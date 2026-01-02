package com.example.athleteregistrationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athleteregistrationapp.ui.RegistrationScreen
import com.example.athleteregistrationapp.ui.AthleteDetailsScreen
import com.example.athleteregistrationapp.ui.theme.AthleteRegistrationAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AthleteRegistrationAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "registration") {
                    composable("registration") {
                        RegistrationScreen(navController)
                    }
                    composable("details/{athleteId}") { backStackEntry ->
                        val athleteId = backStackEntry.arguments?.getString("athleteId") ?: ""
                        AthleteDetailsScreen(navController, athleteId)
                    }
                }
            }
        }
    }
}
