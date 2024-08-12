package com.example.events_management_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination


@Composable
fun EventDetail(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    _id: String,
    login: Boolean,
    initialRegisteredEventsId: List<String>,
) {
    var event by remember { mutableStateOf(Event("", "", "", "", "", "", "", 0, false, "", "")) }
    var join by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }
    var registeredEventsId by remember { mutableStateOf(initialRegisteredEventsId) }

    LaunchedEffect(Unit) {
        event = KtorClient.getEventById(_id)
        if(login)
            registeredEventsId = KtorClient.getRegisteredEvents()
    }

    if (join) {
        LaunchedEffect(Unit) {
            if(event.quota <= 0){
                snackbarHostState.showSnackbar("Join failed: Insufficient Quota.")
                join = false
            }
            else{
                val joinSuccess = KtorClient.JoinEvent(_id)
                if (joinSuccess) {
                    snackbarHostState.showSnackbar("Join successfully")
                    join = false
                    navController.navigate("home")
                } else {
                    snackbarHostState.showSnackbar("Join failed")
                    join = false
                }
            }
        }
    }

    if (delete) {
        LaunchedEffect(Unit) {
            val deleteSuccess = KtorClient.DeleteEvent(_id)
            if (deleteSuccess) {
                snackbarHostState.showSnackbar("Delete successfully")
                delete = false
                navController.navigate("home")
            } else {
                snackbarHostState.showSnackbar("Deletion failed")
                delete = false
            }
        }
    }

    LazyColumn(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        item {
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
            Text(
                text = "Description: ${event.description}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontSize = 18.sp
            )
            Divider()
            Text(
                text = "DateTime: ${event.event_date}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontSize = 18.sp
            )
            Divider()
            Text(
                text = "Location: ${event.location}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontSize = 18.sp
            )
            Divider()
            Text(
                text = "Quota: ${event.quota}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontSize = 18.sp
            )
            Divider()
        }
        if (login == true) {
            var registered = false
            if (event._id in registeredEventsId) {
                registered = true
            }

            if (registered == false) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Button(
                            onClick = {
                                join = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                "Join Event",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            } else {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Button(
                            onClick = {
                                delete = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                "Unregistered",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


