package fr.isen.soubry.isensmartcompanion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.soubry.isensmartcompanion.models.Event
import androidx.compose.ui.text.style.TextAlign

@Composable
fun EventsScreen(navController: NavController, eventsViewModel: EventsViewModel) {
    val events by remember { derivedStateOf { eventsViewModel.events } }
    var searchText by remember { mutableStateOf("") }

    val filteredEvents = events.filter { it.title.contains(searchText, ignoreCase = true) }

    Scaffold(
        topBar = { EventsTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Rechercher un événement", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents) { event ->
                    EventItem(event, navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsTopBar() {
    TopAppBar(
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ISEN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = "Smart Companion",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
fun EventItem(event: Event, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("eventDetail/${event.id}") }, // Rend toute la carte cliquable

        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // ✅ Centre le texte dans la colonne
        ) {
            Text(
                text = event.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB71C1C), // ✅ Rouge ISEN
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center // ✅ Centre le texte horizontalement
            )
        }
    }
}
