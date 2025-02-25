package fr.isen.soubry.isensmartcompanion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import fr.isen.soubry.isensmartcompanion.data.Interaction
import fr.isen.soubry.isensmartcompanion.data.InteractionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(navController: NavController, backStackEntry: NavBackStackEntry, viewModel: InteractionViewModel = viewModel()) {
    val interactionId = backStackEntry.arguments?.getString("interactionId")?.toIntOrNull()
    val interactions by viewModel.allInteractions.collectAsState(initial = emptyList())
    val interaction = interactions.find { it.id == interactionId }

    Scaffold(
        topBar = { HistoryDetailTopBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (interaction == null) {
                Text(
                    text = "Chargement...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            } else {
                HistoryDetailContent(interaction)
            }
        }
    }
}

// 🔹 **Affichage des détails de l'interaction**
@Composable
fun HistoryDetailContent(interaction: Interaction) {
    Spacer(modifier = Modifier.height(16.dp))

    // 🔹 **Affichage de la question en rouge ISEN**
    Text(
        text = interaction.question, // ✅ Suppression du ❓
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFB71C1C) // 🔴 Rouge ISEN, comme EventDetailScreen
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 🏷 **Carte contenant la réponse**
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // 🔴 Fond rouge clair, comme HistoryScreen
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 📅 Icône calendrier en noir et blanc
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Date",
                    tint = Color.Black, // ✅ Icône en noir et blanc
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDate(interaction.date),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ **Affichage de la réponse (sans logo IA)**
            Text(
                text = interaction.answer,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

// 🔹 **Barre du haut pour revenir en arrière**
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailTopBar(navController: NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
        },
        title = { Text("Détail de l'interaction", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
    )
}
