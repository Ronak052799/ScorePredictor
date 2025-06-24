package com.example.peakplaysscorepredictor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.peakplaysscorepredictor.ui.theme.GalaxyBackground
import com.example.peakplaysscorepredictor.ui.viewmodels.ScorePredictorViewModel
import com.example.peakplaysscorepredictor.backend.FootballRepository.Team


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorePredictorScreen(
    navController: NavController,
    viewModel: ScorePredictorViewModel = viewModel()
) {
    var selectedSeason by remember { mutableStateOf("") }
    var homeTeam by remember { mutableStateOf<Team?>(null) }
    var awayTeam by remember { mutableStateOf<Team?>(null) }

    val availableTeams by viewModel.availableTeams.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val predictedResult by viewModel.predictedResult.collectAsState()
    val recentPredictions by viewModel.recentPredictions.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        GalaxyBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
            topBar = {
                TopAppBar(
                    title = { Text("Premier League Score Predictor", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    )
                )
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    OutlinedTextField(
                        value = selectedSeason,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                selectedSeason = newValue
                            }
                        },
                        label = { Text("Enter Season (e.g. 2023)", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                            cursorColor = Color.White
                        )
                    )
                }

                item {
                    Button(
                        onClick = {
                            val seasonInt = selectedSeason.toIntOrNull()
                            if (seasonInt == null) {
                                viewModel.fetchTeamsForSeason(-1) // reset or error state
                            } else {
                                viewModel.fetchTeamsForSeason(seasonInt)
                                homeTeam = null
                                awayTeam = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Fetch Teams")
                    }
                }

                if (availableTeams.isNotEmpty()) {
                    item {
                        TeamDropdown(
                            label = "Home Team",
                            teams = availableTeams,
                            selectedTeam = homeTeam,
                            onTeamSelected = { homeTeam = it }
                        )
                    }

                    item {
                        TeamDropdown(
                            label = "Away Team",
                            teams = availableTeams,
                            selectedTeam = awayTeam,
                            onTeamSelected = { awayTeam = it }
                        )
                    }
                }

                if (homeTeam != null && awayTeam != null) {
                    item {
                        Button(
                            onClick = {
                                val seasonInt = selectedSeason.toIntOrNull()
                                if (seasonInt != null) {
                                    viewModel.predictWinner(homeTeam!!, awayTeam!!, seasonInt)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Predict Winner")
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    item {
                        Text(errorMessage, color = Color.Red)
                    }
                }

                predictedResult?.let { prediction ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Predicted Winner", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text(
                                    "${prediction.predictedWinner} (${String.format("%.1f", prediction.confidence)}%)",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                if (recentPredictions.isNotEmpty()) {
                    item {
                        Text("Recent Predictions", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }

                    items(recentPredictions.reversed()) { prediction ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = "${prediction.homeTeam} vs ${prediction.awayTeam}: Winner - ${prediction.predictedWinner} (${String.format("%.1f", prediction.confidence)}%)",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamDropdown(
    label: String,
    teams: List<Team>,
    selectedTeam: Team?,
    onTeamSelected: (Team) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedTeam?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                cursorColor = Color.White
            )
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            teams.forEach { team ->
                DropdownMenuItem(
                    text = { Text(team.name) },
                    onClick = {
                        onTeamSelected(team)
                        expanded = false
                    }
                )
            }
        }
    }
}
