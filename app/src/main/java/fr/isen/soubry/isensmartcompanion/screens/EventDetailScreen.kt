package fr.isen.soubry.isensmartcompanion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import fr.isen.soubry.isensmartcompanion.models.Event
import fr.isen.soubry.isensmartcompanion.screens.fakeEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(navController: NavController, backStackEntry: NavBackStackEntry) {
    val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
    val event = fakeEvents.find { it.id == eventId }

    Scaffold(
        topBar = { EventDetailTopBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (event != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // Titre de l'événement
                Text(
                    text = event.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C) // Rouge foncé
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Carte contenant les détails de l'événement
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE) // Fond rouge très clair
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        DetailItem(label = "Date", value = event.date, icon = Icons.Default.CalendarToday)
                        DetailItem(label = "Lieu", value = event.location, icon = Icons.Default.LocationOn)
                        DetailItem(label = "Catégorie", value = event.category, icon = Icons.Default.Category)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = event.description,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
            } else {
                Text(
                    "Événement introuvable",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black, // Icônes en noir
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Catégories en gras et noir
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailTopBar(navController: NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        },
        title = {
            Text(
                text = "Détails de l'événement",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    )
}
