package com.example.events_management_system

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, initialEvents: List<Event>, isLoggedIn: Boolean) {
    var events by remember { mutableStateOf(initialEvents) }
    var page by remember { mutableStateOf(1) }
    var loadingNewPage by remember { mutableStateOf(false) }

    LaunchedEffect(page) {
        if (!loadingNewPage) {
            loadingNewPage = true
            val newEvents = KtorClient.getEvents(page)
            events = if (page == 1) newEvents else events + newEvents
            loadingNewPage = false
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val sortedEvents = events.sortedByDescending { it.highlight }
        items(sortedEvents) { event ->
            Card(
                onClick = { navController.navigate("eventDetails/${event._id}/$isLoggedIn") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column {
                    AsyncImage(
                        model = event.image,
                        contentDescription = "Image for ${event.title}",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        text = "Title: ${event.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Organizer: ${event.organiser}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Start,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
            Divider()
        }
        item {
            if (loadingNewPage) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (!loadingNewPage) page++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !loadingNewPage
                ) {
                    Text("Load More")
                }
            }
        }
    }
}