package com.example.peakplaysscorepredictor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.peakplaysscorepredictor.backend.FootballRepository
import com.example.peakplaysscorepredictor.backend.APIResponseWrapper
import com.example.peakplaysscorepredictor.ui.theme.GalaxyBackground
import com.example.peakplaysscorepredictor.ui.viewmodels.ScorePredictorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorePredictorScreen(
    navController: NavController,
    viewModel: ScorePredictorViewModel = viewModel()
) {
    val footballRepo = remember { FootballRepository() }
    var fetchStatus by remember { mutableStateOf("Idle") }
    var playerData by remember { mutableStateOf<Map<String, List<APIResponseWrapper.PlayerWrapper>>>(emptyMap()) }
    var homeTeam by remember { mutableStateOf("") }
    var awayTeam by remember { mutableStateOf("") }
    var predictedScore by remember { mutableStateOf("") }
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
                                contentDescription = "Back to home",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter Teams",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = homeTeam,
                    onValueChange = { homeTeam = it },
                    label = { Text("Home Team", color = Color.White.copy(alpha = 0.9f)) },
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

                OutlinedTextField(
                    value = awayTeam,
                    onValueChange = { awayTeam = it },
                    label = { Text("Away Team", color = Color.White.copy(alpha = 0.9f)) },
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

                Button(
                    onClick = {
                        predictedScore = viewModel.predictScore(homeTeam, awayTeam)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = homeTeam.isNotBlank() && awayTeam.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Predict Score")
                }

                // ===== New button to test API call =====
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        fetchStatus = "Fetching data..."
                        footballRepo.fetchTeamsAndPlayerStats(2023, object : FootballRepository.DataCallback {
                            override fun onSuccess(teamPlayerMap: Map<String, List<com.example.peakplaysscorepredictor.backend.APIResponseWrapper.PlayerWrapper>>) {
                                fetchStatus = "Fetched data for ${teamPlayerMap.size} teams"
                                playerData = teamPlayerMap
                                android.util.Log.d("ScorePredictorScreen", fetchStatus)
                            }

                            override fun onError(error: String) {
                                fetchStatus = "Error: $error"
                                android.util.Log.e("ScorePredictorScreen", error)
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                        contentColor = Color.White
                    )

                ) {
                    Text("Fetch Player Stats (Test API)")
                }

                Text(
                    text = fetchStatus,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (playerData.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        playerData.forEach { (teamName, players) ->
                            item {
                                Text(
                                    text = "Team: $teamName",
                                    color = Color.Yellow,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(players.take(5), key = { it.player.id }) { playerWrapper ->
                                val player = playerWrapper.player
                                val stats = playerWrapper.statistics.firstOrNull()

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Text("• ${player.name} (${stats?.games?.position ?: "N/A"})", color = Color.White)
                                    Text("  - Goals: ${stats?.goals?.total ?: "N/A"}", color = Color.White)
                                    Text("  - Assists: ${stats?.goals?.assists ?: "N/A"}", color = Color.White)
                                    Text("  - Rating: ${stats?.games?.rating ?: "N/A"}", color = Color.White)
                                }
                            }
                        }
                    }
                }

                // =====================================

                if (predictedScore.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Predicted Score",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = predictedScore,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                if (recentPredictions.isNotEmpty()) {
                    Text(
                        text = "Recent Predictions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentPredictions.reversed()) { prediction ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                )
                            ) {
                                Text(
                                    text = "${prediction.homeTeam} vs ${prediction.awayTeam}: ${prediction.score}",
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
} 