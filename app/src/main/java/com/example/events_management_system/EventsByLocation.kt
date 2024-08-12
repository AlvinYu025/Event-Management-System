package com.example.events_management_system

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun EventsByLocation(
    navController: NavHostController,
    locationNumber: Int,
    isLoggedIn: Boolean
) {
    var events by remember { mutableStateOf(listOf<Event>()) }
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(page) {
        isLoading = true
        try {
            val newEvents = KtorClient.getEventsByLocation(page, locationNumber)
            if (page == 1) {
                events = newEvents
            } else {
                events += newEvents
            }
        } finally {
            isLoading = false
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(events) { event ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("eventDetails/${event._id}/$isLoggedIn") }
                    .padding(8.dp)
            ) {
                Text(
                    text = "Title: ${event.title}",
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Address: ${event.location}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Divider()
            }
        }
        item {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (!isLoading && events.isNotEmpty()) {
                            page++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isLoading
                ) {
                    Text("Load More")
                }
            }
        }
    }
}

@Composable
fun LocationList(navController: NavHostController, isLoggedIn: Boolean) {
    LazyColumn {
        items((1..9).toList()) { number ->
            ListItem(
                headlineContent = { Text(number.toString()) },
                modifier = Modifier.clickable {
                    navController.navigate("eventsByLocation/${number}/$isLoggedIn")
                }
            )
            Divider()
        }
    }
}
