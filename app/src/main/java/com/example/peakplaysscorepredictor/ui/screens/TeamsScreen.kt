package com.example.peakplaysscorepredictor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.peakplaysscorepredictor.backend.APIPremierLeagueTeams
import com.example.peakplaysscorepredictor.ui.theme.GalaxyBackground
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(navController: NavController) {
    var teams by remember { mutableStateOf<List<APIPremierLeagueTeams.TeamData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Fallback Premier League teams in case API fails
    val fallbackTeams = listOf(
        "Arsenal", "Aston Villa", "Bournemouth", "Brentford", "Brighton & Hove Albion",
        "Burnley", "Chelsea", "Crystal Palace", "Everton", "Fulham",
        "Liverpool", "Luton Town", "Manchester City", "Manchester United", "Newcastle United",
        "Nottingham Forest", "Sheffield United", "Tottenham Hotspur", "West Ham United", "Wolverhampton Wanderers"
    )
    
    // Fetch teams when the screen is first displayed
    LaunchedEffect(Unit) {
        val apiService = com.example.peakplaysscorepredictor.backend.APIClient.getAPIService()
        val call = apiService.getTeams(39, 2024, com.example.peakplaysscorepredictor.backend.APIClient.getApiKey())
        
        call.enqueue(object : Callback<APIPremierLeagueTeams> {
            override fun onResponse(call: Call<APIPremierLeagueTeams>, response: Response<APIPremierLeagueTeams>) {
                if (response.isSuccessful && response.body() != null) {
                    teams = response.body()!!.response
                    isLoading = false
                } else {
                    // If API fails, use fallback teams
                    teams = fallbackTeams.map { teamName ->
                        APIPremierLeagueTeams.TeamData().apply {
                            team = APIPremierLeagueTeams.Team().apply {
                                id = fallbackTeams.indexOf(teamName)
                                name = teamName
                                logo = "" // No logo for fallback teams
                            }
                        }
                    }
                    isLoading = false
                }
            }
            
            override fun onFailure(call: Call<APIPremierLeagueTeams>, t: Throwable) {
                // If network fails, use fallback teams
                teams = fallbackTeams.map { teamName ->
                    APIPremierLeagueTeams.TeamData().apply {
                        team = APIPremierLeagueTeams.Team().apply {
                            id = fallbackTeams.indexOf(teamName)
                            name = teamName
                            logo = "" // No logo for fallback teams
                        }
                    }
                }
                isLoading = false
            }
        })
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GalaxyBackground()
        
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
            topBar = {
                TopAppBar(
                    title = { Text("Premier League Teams", color = Color.White) },
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(teams) { teamData ->
                            TeamCard(team = teamData.team)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamCard(team: APIPremierLeagueTeams.Team) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { /* Handle team selection */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Team Logo
            if (team.logo.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(team.logo),
                    contentDescription = "${team.name} logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                // Placeholder for teams without logos
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = team.name.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Team Name
            Text(
                text = team.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 