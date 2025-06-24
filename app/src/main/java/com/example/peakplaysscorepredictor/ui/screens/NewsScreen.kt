package com.example.peakplaysscorepredictor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.peakplaysscorepredictor.ui.theme.GalaxyBackground
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class NewsItem(
    val title: String,
    val description: String,
    val source: String,
    val publishedAt: String,
    val url: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    var newsItems by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    // Sample Premier League news data
    val sampleNews = listOf(
        NewsItem(
            title = "Manchester City Extends Lead at Top of Premier League",
            description = "Pep Guardiola's side secured another crucial victory to maintain their position at the summit of the Premier League table.",
            source = "Premier League Official",
            publishedAt = "2 hours ago"
        ),
        NewsItem(
            title = "Liverpool's Title Challenge Strengthens with Late Winner",
            description = "A dramatic stoppage-time goal keeps Liverpool in the title race as the season reaches its climax.",
            source = "Sky Sports",
            publishedAt = "4 hours ago"
        ),
        NewsItem(
            title = "Arsenal's Young Stars Shine in London Derby",
            description = "Bukayo Saka and Martin Ødegaard lead Arsenal to an impressive victory in the capital.",
            source = "BBC Sport",
            publishedAt = "6 hours ago"
        ),
        NewsItem(
            title = "Chelsea Appoints New Manager for Next Season",
            description = "The Blues have confirmed their new head coach as they look to rebuild for the upcoming campaign.",
            source = "Chelsea FC Official",
            publishedAt = "1 day ago"
        ),
        NewsItem(
            title = "Premier League Announces New Broadcasting Deal",
            description = "Record-breaking television rights agreement ensures continued growth of the world's most popular league.",
            source = "Premier League Official",
            publishedAt = "2 days ago"
        ),
        NewsItem(
            title = "Manchester United's Transfer Window Plans Revealed",
            description = "Erik ten Hag outlines his strategy for strengthening the squad during the summer transfer window.",
            source = "Manchester Evening News",
            publishedAt = "3 days ago"
        ),
        NewsItem(
            title = "Tottenham Hotspur's Stadium Expansion Plans",
            description = "The North London club announces ambitious plans to increase capacity at their state-of-the-art stadium.",
            source = "Tottenham Hotspur Official",
            publishedAt = "4 days ago"
        ),
        NewsItem(
            title = "Premier League Player of the Month Award Winners",
            description = "Latest monthly awards recognize outstanding performances across the league.",
            source = "Premier League Official",
            publishedAt = "5 days ago"
        ),
        NewsItem(
            title = "Newcastle United's European Ambitions",
            description = "The Magpies continue their impressive form as they push for European qualification.",
            source = "Chronicle Live",
            publishedAt = "1 week ago"
        ),
        NewsItem(
            title = "Premier League Youth Development Success",
            description = "Latest statistics show record numbers of academy graduates making their mark in the top flight.",
            source = "Premier League Official",
            publishedAt = "1 week ago"
        )
    )
    
    // Simulate loading and then populate with sample news
    LaunchedEffect(Unit) {
        // Simulate API call delay
        kotlinx.coroutines.delay(1500)
        newsItems = sampleNews
        isLoading = false
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GalaxyBackground()
        
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
            topBar = {
                TopAppBar(
                    title = { Text("Premier League News", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back to home",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                isLoading = true
                                // Simulate refresh
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(1000)
                                    newsItems = sampleNews.shuffled() // Shuffle for demo
                                    isLoading = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh news",
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = Color.White)
                            Text(
                                text = "Loading latest news...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(newsItems) { newsItem ->
                            NewsCard(newsItem = newsItem)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(newsItem: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // News Title
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // News Description
            Text(
                text = newsItem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Source and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = newsItem.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = newsItem.publishedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
} 