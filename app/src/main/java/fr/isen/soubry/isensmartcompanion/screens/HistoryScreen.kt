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

@Composable
fun HistoryScreen(navController: NavController, viewModel: InteractionViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val interactionHistory by viewModel.allInteractions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { HistoryTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(interactionHistory) { interaction ->
                    HistoryItem(interaction, viewModel, coroutineScope, navController)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton pour supprimer tout l'historique
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deleteAllInteractions()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), // 🔴 Rouge ISEN
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Effacer tout l'historique", color = Color.White)
            }
        }
    }
}

// 🔹 **Barre du haut identique à EventsScreen**
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
                    color = Color(0xFFB71C1C) // 🔴 Rouge ISEN
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
            .wrapContentSize() // ✅ Ajuste la taille au contenu
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("historyDetail/${interaction.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // 🔴 Fond rouge clair
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 🔹 Icône calendrier en noir et blanc + Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date",
                    tint = Color.Black, // ✅ Icône en noir
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

            // 🔹 Question en noir et en gras
            Text(
                text = interaction.question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 🔹 Alignement du bouton suppression à droite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        viewModel.deleteInteraction(interaction)
                    }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer", tint = Color.Black)
                }
            }
        }
    }
}


// Fonction pour formater la date
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
