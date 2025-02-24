package fr.isen.soubry.isensmartcompanion.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun AssistantScreen() {
    var question by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("Votre réponse apparaîtra ici...") }
    var interactionHistory by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Modèle Gemini AI
    val generativeModel = GenerativeModel("gemini-1.5-flash", "AIzaSyC2AJeEPfFp3tm2xbeyDaIaR_VDMffE0nI")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 📌 Titre ISEN Smart Companion
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ISEN",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C) // 🔴 Rouge ISEN
                )
                Text(
                    text = "Smart Companion",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            // 📩 Affichage de l'historique des interactions (questions et réponses)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)  // Permet de faire de la place pour le champ de texte et le bouton
            ) {
                items(interactionHistory) { interaction ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text("Vous : ${interaction.first}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("IA : ${interaction.second}", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            }
        }

        // 📩 Champ de texte + bouton envoyer en bas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.BottomCenter),  // Positionne le champ de texte et bouton en bas
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✏️ Champ de saisie
            TextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Posez votre question...") },
                textStyle = TextStyle(fontSize = 16.sp),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            // 📩 Bouton envoyer
            Button(
                onClick = {
                    if (question.isNotEmpty()) {
                        // Ajouter la question à l'historique
                        val currentQuestion = question

                        // Envoie de la question à Gemini AI
                        coroutineScope.launch(Dispatchers.IO) {
                            val aiAnswer = getAIResponse(generativeModel, currentQuestion)
                            withContext(Dispatchers.Main) {
                                // Mise à jour de l'historique des interactions
                                interactionHistory = interactionHistory + Pair(currentQuestion, aiAnswer)
                            }
                        }
                        Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()
                        question = "" // 🔄 Efface la question après envoi
                    } else {
                        Toast.makeText(context, "Veuillez entrer une question", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape), // 🔘 Rond comme sur ton design
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)) // 🔴 Rouge ISEN
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Envoyer",
                    tint = Color.White
                )
            }
        }
    }
}

// Fonction pour interroger Gemini AI
private suspend fun getAIResponse(generativeModel: GenerativeModel, input: String): String {
    return try {
        // Appel à Gemini AI pour obtenir la réponse
        val response = generativeModel.generateContent(input)
        response.text ?: "Aucune réponse obtenue"
    } catch (e: Exception) {
        "Erreur: ${e.message}"
    }
}