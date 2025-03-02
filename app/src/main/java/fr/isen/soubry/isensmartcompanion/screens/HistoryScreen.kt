package fr.isen.soubry.isensmartcompanion.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.soubry.isensmartcompanion.data.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun HistoryScreen(viewModel: InteractionViewModel, navController: NavController) {
    val interactions by viewModel.allInteractions.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    // Filtrage des interactions en fonction du texte de recherche et du filtre favoris
    val filteredInteractions = interactions.filter {
        it.question.contains(searchText, ignoreCase = true) &&
                (!showFavoritesOnly || it.isFavorite) // Filtrer les favoris si activé
    }

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
            // 🔍 Champ de recherche
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Rechercher une question", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ⭐ Bouton pour filtrer uniquement les favoris
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showFavoritesOnly,
                    onCheckedChange = { showFavoritesOnly = it }
                )
                Text("Afficher uniquement les favoris", fontSize = 16.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton "Supprimer tout l'historique"
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.clearHistory()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), // Rouge ISEN
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Supprimer tout l'historique", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredInteractions.isEmpty()) {
                Text("Aucune question trouvée.", fontSize = 18.sp, color = Color.Gray)
            } else {
                LazyColumn {
                    items(filteredInteractions) { interaction ->
                        HistoryItem(interaction, viewModel, coroutineScope, navController)
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
fun HistoryItem(
    interaction: Interaction,
    viewModel: InteractionViewModel,
    coroutineScope: CoroutineScope,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("historyDetail/${interaction.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Fond rouge clair
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // ⭐ & 🗑 Groupe des icônes en haut à droite
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ⭐ Bouton étoile (favoris)
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.toggleFavorite(interaction)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (interaction.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (interaction.isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                        tint = Color(0xFFB71C1C) // 🔴 Rouge ISEN
                    )
                }

                // 🗑 Bouton poubelle (supprimer)
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.deleteInteraction(interaction)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Supprimer",
                        tint = Color(0xFFB71C1C) // 🔴 Poubelle rouge ISEN
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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




// Fonction pour formater la date
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
