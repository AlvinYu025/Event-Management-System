package com.example.events_management_system

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavController, initialEvents: List<Event>, isLoggedIn: Boolean) {
    var searchText by remember { mutableStateOf("") }
    var events by remember { mutableStateOf(initialEvents) }
    var page by remember { mutableStateOf(1) }
    var loadingNewPage by remember { mutableStateOf(false) }

    LaunchedEffect(searchText, page) {
        loadingNewPage = true
        val newEvents = KtorClient.getEventBySearch(searchText, page)
        events = if (page == 1) newEvents else events + newEvents
        loadingNewPage = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(searchText) { newText ->
            searchText = newText
            page = 1 // Reset page to 1 whenever search text changes
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
                        Text("Title: ${event.title}", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                        Text("Organizer: ${event.organiser}", modifier = Modifier.padding(8.dp))
                        Text("Description: ${event.description}", modifier = Modifier.padding(horizontal = 8.dp))
                        Divider()
                    }
                }
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
                    ) {
                        Text("Load More")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(searchText: String, onSearchChanged: (String) -> Unit) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchChanged,
        label = { Text("Search Events") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearchChanged(searchText) // Triggers re-search with current text
        }),
        trailingIcon = {
            IconButton(onClick = { onSearchChanged(searchText) }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        }
    )
}

