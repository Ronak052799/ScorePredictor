package com.example.peakplaysscorepredictor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peakplaysscorepredictor.backend.*
import com.example.peakplaysscorepredictor.backend.FootballRepository.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Prediction(
    val homeTeam: String,
    val awayTeam: String,
    val predictedWinner: String,
    val confidence: Double
)

class ScorePredictorViewModel : ViewModel() {

    private val footballRepo = FootballRepository()
    private val matchPredictor = MatchPredictor()

    private val _availableTeams = MutableStateFlow<List<Team>>(emptyList())
    val availableTeams: StateFlow<List<Team>> = _availableTeams.asStateFlow()

    private val _recentPredictions = MutableStateFlow<List<Prediction>>(emptyList())
    val recentPredictions: StateFlow<List<Prediction>> = _recentPredictions.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _predictedResult = MutableStateFlow<Prediction?>(null)
    val predictedResult: StateFlow<Prediction?> = _predictedResult.asStateFlow()

    fun fetchTeamsForSeason(season: Int) {
        _errorMessage.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            footballRepo.fetchTeams(season, object : FootballRepository.TeamsCallback {
                override fun onSuccess(teams: List<Team>) {
                    viewModelScope.launch(Dispatchers.Main) {
                        _availableTeams.value = teams.sortedBy { it.name }
                        // Optionally fetch player stats in background to populate ratings cache
                        footballRepo.fetchPlayerStatsForAllTeams(teams, season, object : FootballRepository.DataCallback {
                            override fun onSuccess(teamPlayerMap: Map<Int, List<APIResponseWrapper.PlayerWrapper>>) {
                                // No UI update needed here
                            }
                            override fun onError(error: String) {
                                _errorMessage.value = "Error fetching player stats: $error"
                            }
                        })
                    }
                }

                override fun onError(error: String) {
                    viewModelScope.launch(Dispatchers.Main) {
                        _errorMessage.value = error
                    }
                }
            })
        }
    }

    fun predictWinner(
        homeTeam: Team,
        awayTeam: Team,
        season: Int
    ) {
        _errorMessage.value = ""
        _predictedResult.value = null

        if (homeTeam.id == awayTeam.id) {
            _errorMessage.value = "Home and Away teams must be different"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            footballRepo.fetchPredictionData(homeTeam.id, awayTeam.id, season, object : FootballRepository.PredictionDataCallback {
                override fun onSuccess(
                    homeTeamRating: Double,
                    awayTeamRating: Double,
                    recentHomeFixtures: List<FixtureResponseWrapper.FixtureData>,
                    recentAwayFixtures: List<FixtureResponseWrapper.FixtureData>,
                    headToHeadFixtures: List<FixtureResponseWrapper.FixtureData>
                ) {
                    val result = matchPredictor.predictWinner(
                        homeTeam.name,
                        awayTeam.name,
                        homeTeamRating,
                        awayTeamRating,
                        recentHomeFixtures,
                        recentAwayFixtures,
                        headToHeadFixtures
                    )

                    val prediction = Prediction(
                        homeTeam = homeTeam.name,
                        awayTeam = awayTeam.name,
                        predictedWinner = result.winner,
                        confidence = result.confidenceScore
                    )

                    viewModelScope.launch(Dispatchers.Main) {
                        _predictedResult.value = prediction
                        _recentPredictions.value = _recentPredictions.value + prediction
                    }
                }

                override fun onError(error: String) {
                    viewModelScope.launch(Dispatchers.Main) {
                        _errorMessage.value = error
                    }
                }
            })
        }
    }
}
