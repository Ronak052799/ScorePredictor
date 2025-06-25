package com.example.peakplaysscorepredictor

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.peakplaysscorepredictor.ui.screens.HomeScreen
import com.example.peakplaysscorepredictor.ui.screens.ScorePredictorScreen
import com.example.peakplaysscorepredictor.ui.screens.TeamsScreen
import com.example.peakplaysscorepredictor.ui.screens.NewsScreen
import com.example.peakplaysscorepredictor.ui.screens.MatchPreviewScreen

@Composable
fun ScorePredictorApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("score_predictor") {
            ScorePredictorScreen(navController)
        }
        composable("teams") {
            TeamsScreen(navController)
        }
        composable("news") {
            NewsScreen(navController)
        }
        composable("match_preview") {
            MatchPreviewScreen(navController)
        }
    }
} 