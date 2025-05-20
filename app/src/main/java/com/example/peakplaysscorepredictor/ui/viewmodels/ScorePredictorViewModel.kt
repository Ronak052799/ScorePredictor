package com.example.peakplaysscorepredictor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Prediction(
    val homeTeam: String,
    val awayTeam: String,
    val score: String
)

class ScorePredictorViewModel : ViewModel() {
    private val _recentPredictions = MutableStateFlow<List<Prediction>>(emptyList())
    val recentPredictions: StateFlow<List<Prediction>> = _recentPredictions.asStateFlow()

    fun predictScore(homeTeam: String, awayTeam: String): String {
        if (homeTeam.isBlank() || awayTeam.isBlank()) {
            return ""
        }

        // Simple random prediction for now
        // TODO: Implement more sophisticated prediction algorithm
        val homeGoals = Random.nextInt(0, 4)
        val awayGoals = Random.nextInt(0, 4)
        val prediction = "$homeGoals-$awayGoals"

        // Add to recent predictions
        viewModelScope.launch {
            val newPrediction = Prediction(homeTeam, awayTeam, prediction)
            _recentPredictions.value = _recentPredictions.value + newPrediction
        }

        return prediction
    }
} 