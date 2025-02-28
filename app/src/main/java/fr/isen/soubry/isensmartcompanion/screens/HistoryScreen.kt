package fr.isen.soubry.isensmartcompanion.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.isen.soubry.isensmartcompanion.data.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: InteractionViewModel, navController: NavController) {
    val interactions by viewModel.allInteractions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historique",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB71C1C) // 🔴 Texte rouge ISEN
                    )
                },
                actions = {
                    // Bouton "Supprimer tout l'historique"
                    IconButton(onClick = {
                        viewModel.clearHistory() // ✅ Supprime tout l'historique
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Supprimer tout",
                            tint = Color(0xFFB71C1C) // 🔴 Poubelle rouge ISEN
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (interactions.isEmpty()) {
                Text("Aucun historique pour le moment.", fontSize = 18.sp, color = Color.Gray)
            } else {
                LazyColumn {
                    items(interactions) { interaction ->
                        HistoryItem(interaction, viewModel, rememberCoroutineScope(), navController)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar() {
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
fun HistoryItem(interaction: Interaction, viewModel: InteractionViewModel, coroutineScope: CoroutineScope, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight() // ✅ Ajuste la hauteur dynamiquement
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("historyDetail/${interaction.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Rouge clair
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Bouton poubelle en haut à droite
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deleteInteraction(interaction)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd) // ✅ Aligné en haut à droite
                    .padding(8.dp) // ✅ Ajoute un petit espace
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Supprimer",
                    tint = Color(0xFFB71C1C) // 🔴 Poubelle rouge ISEN
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(interaction.date),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = interaction.question,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}



fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

