package com.example.events_management_system

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoggedIn by remember { mutableStateOf(false) }
    var initialRegisteredEventsId by remember { mutableStateOf((emptyList<String>())) }
    var title by remember { mutableStateOf("Events Management System") }

    val items = if (isLoggedIn) {
        listOf("Home", "Events", "Search", "User")
    } else {
        listOf("Home", "Events", "Search", "Login")
    }

    val itemIcons = if (isLoggedIn) {
        listOf(
            Icons.Filled.Home,
            Icons.Filled.DateRange,
            Icons.Filled.Search,
            Icons.Filled.AccountCircle
        )
    } else {
        listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.Filled.Search, Icons.Filled.Person)
    }

    val feeds by produceState(
        initialValue = listOf<Event>(),
        producer = {
            value = KtorClient.getEvents(1)
        }
    )

    var userEvents = emptyList<Event>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(itemIcons[index], contentDescription = item) },
                        modifier = Modifier.testTag(item),
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (item == "Login") {
                                navController.navigate("login") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else if (item == "User") {
                                navController.navigate("user") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                navController.navigate(item.lowercase()) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeScreen(navController, feeds, isLoggedIn)
                        title = "Events Management System"
                    }
                    composable("events") {
                        LocationList(navController, isLoggedIn)
                        title = "Events"
                    }
                    composable("search") {
                        Search(navController, feeds, isLoggedIn)
                        title = "Search"
                    }
                    composable("login") {
                        LoginPage(navController, snackbarHostState) {
                            isLoggedIn = true
                            initialRegisteredEventsId = KtorClient.getRegisteredEvents()
                            userEvents = KtorClient.getUserEvents(1)
                        }
                        title = "Log In"
                    }

                    composable("user") {
                        UserScreen(navController, snackbarHostState, userEvents)
                        title = "User"
                    }

                    composable("becomeVolunteer") {
                        BecomeVolunteer(navController, snackbarHostState)
                        title = "Become a volunteer"
                    }

                    composable(
                        "eventsByLocation/{locationNumber}/{isLoggedIn}",
                        arguments = listOf(
                            navArgument("locationNumber") { type = NavType.IntType },
                            navArgument("isLoggedIn") { type = NavType.BoolType })
                    ) { backStackEntry ->
                        val locationNumber = backStackEntry.arguments?.getInt("locationNumber") ?: 1
                        val login = backStackEntry.arguments?.getBoolean("isLoggedIn") ?: false
                        EventsByLocation(navController, locationNumber, login)
                        title = "Address ${locationNumber}"
                    }

                    composable(
                        "eventDetails/{_id}/{isLoggedIn}",
                        arguments = listOf(
                            navArgument("_id") { type = NavType.StringType },
                            navArgument("isLoggedIn") { type = NavType.BoolType }
                        )
                    ) { backStackEntry ->
                        val _id = backStackEntry.arguments?.getString("_id") ?: ""
                        val login = backStackEntry.arguments?.getBoolean("isLoggedIn") ?: false
                        EventDetail(
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            _id = _id,
                            login = login,
                            initialRegisteredEventsId = initialRegisteredEventsId
                        )
                        title = "Event Detail"
                    }

                    composable("logout") {
                        isLoggedIn = false
                        navController.navigate("login")
                    }
                }
            }
        }
    )
}