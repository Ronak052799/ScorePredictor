package com.example.peakplaysscorepredictor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorePredictorScreen(
    navController: NavController,
    viewModel: ScorePredictorViewModel = viewModel()
) {
    var homeTeam by remember { mutableStateOf("") }
    var awayTeam by remember { mutableStateOf("") }
    var predictedScore by remember { mutableStateOf("") }
    val recentPredictions by viewModel.recentPredictions.collectAsState()
    
    // Dropdown states
    var homeTeamExpanded by remember { mutableStateOf(false) }
    var awayTeamExpanded by remember { mutableStateOf(false) }
    
    // Premier League teams list
    val premierLeagueTeams = listOf(
        "Arsenal",
        "Aston Villa", 
        "Bournemouth",
        "Brentford",
        "Brighton & Hove Albion",
        "Burnley",
        "Chelsea",
        "Crystal Palace",
        "Everton",
        "Fulham",
        "Liverpool",
        "Luton Town",
        "Manchester City",
        "Manchester United",
        "Newcastle United",
        "Nottingham Forest",
        "Sheffield United",
        "Tottenham Hotspur",
        "West Ham United",
        "Wolverhampton Wanderers"
    )

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
                    text = "Select Teams",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Home Team Dropdown
                ExposedDropdownMenuBox(
                    expanded = homeTeamExpanded,
                    onExpandedChange = { homeTeamExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = homeTeam,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Home Team", color = Color.White.copy(alpha = 0.9f)) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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
                    
                    ExposedDropdownMenu(
                        expanded = homeTeamExpanded,
                        onDismissRequest = { homeTeamExpanded = false },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.9f))
                    ) {
                        premierLeagueTeams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team, color = Color.White) },
                                onClick = {
                                    homeTeam = team
                                    homeTeamExpanded = false
                                },
                                modifier = Modifier.background(
                                    if (homeTeam == team) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    else Color.Transparent
                                )
                            )
                        }
                    }
                }

                // Away Team Dropdown
                ExposedDropdownMenuBox(
                    expanded = awayTeamExpanded,
                    onExpandedChange = { awayTeamExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = awayTeam,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Away Team", color = Color.White.copy(alpha = 0.9f)) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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
                    
                    ExposedDropdownMenu(
                        expanded = awayTeamExpanded,
                        onDismissRequest = { awayTeamExpanded = false },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.9f))
                    ) {
                        premierLeagueTeams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team, color = Color.White) },
                                onClick = {
                                    awayTeam = team
                                    awayTeamExpanded = false
                                },
                                modifier = Modifier.background(
                                    if (awayTeam == team) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    else Color.Transparent
                                )
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        predictedScore = viewModel.predictScore(homeTeam, awayTeam)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = homeTeam.isNotBlank() && awayTeam.isNotBlank() && homeTeam != awayTeam,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Predict Score")
                }

                // Show warning if same team is selected
                if (homeTeam.isNotBlank() && awayTeam.isNotBlank() && homeTeam == awayTeam) {
                    Text(
                        text = "Please select different teams for home and away",
                        color = Color.Red.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

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
