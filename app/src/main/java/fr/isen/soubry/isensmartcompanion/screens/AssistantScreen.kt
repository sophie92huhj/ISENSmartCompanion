package fr.isen.soubry.isensmartcompanion.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward// ✅ Nouvelle icône de flèche
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.soubry.isensmartcompanion.data.Interaction
import fr.isen.soubry.isensmartcompanion.data.InteractionViewModel

@Composable
fun AssistantScreen(viewModel: InteractionViewModel = viewModel()) {
    var question by remember { mutableStateOf("") }
    var lastQuestion by remember { mutableStateOf<String?>(null) } // ✅ Stocke la dernière question affichée
    var aiResponse by remember { mutableStateOf<String?>(null) } // ✅ Stocke uniquement la réponse actuelle
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Modèle Gemini AI
    val generativeModel = GenerativeModel("gemini-1.5-flash", "AIzaSyCR5oF0w1NqV_y6RFnJicqSj84yaGL2Eto")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 📌 **Titre ISEN Smart Companion agrandi**
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ISEN",
                    fontSize = 42.sp, // ✅ **Agrandir la taille**
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C) // 🔴 Rouge ISEN
                )
                Text(
                    text = "Smart Companion",
                    fontSize = 22.sp, // ✅ **Agrandir la taille**
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // ✅ **Espacer plus la question/réponse**

            // 📩 **Affichage de la dernière interaction (question + réponse)**
            if (lastQuestion != null && aiResponse != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Vous : $lastQuestion", // ✅ La question est maintenant bien affichée !
                        fontSize = 18.sp, // ✅ Légèrement plus grand
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp)) // ✅ Plus d’espace
                    Text(
                        text = "IA : $aiResponse",
                        fontSize = 18.sp, // ✅ Légèrement plus grand
                        color = Color.Gray
                    )
                }
            }
        }

        // 📩 **Champ de texte + bouton envoyer en bas**
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✏️ **Champ de saisie**
            TextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Posez votre question...") },
                textStyle = TextStyle(fontSize = 16.sp),
                singleLine = true,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            // 📩 **Bouton envoyer avec flèche rouge**
            Button(
                onClick = {
                    if (question.isNotEmpty()) {
                        val currentQuestion = question // ✅ Stocker avant d’effacer
                        question = "" // 🔄 Efface la zone de texte

                        coroutineScope.launch(Dispatchers.IO) {
                            // 🔹 **Envoyer la requête à Gemini AI**
                            val aiResponseText = getAIResponse(generativeModel, currentQuestion)

                            withContext(Dispatchers.Main) {
                                // ✅ **Met à jour la question et la réponse sans effacer**
                                lastQuestion = currentQuestion
                                aiResponse = aiResponseText
                            }

                            // 🔹 **Sauvegarde de l'interaction dans l'historique**
                            viewModel.insertInteraction(currentQuestion, aiResponseText)
                        }

                        Toast.makeText(context, "Question envoyée", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Veuillez entrer une question", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.size(50.dp).clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward, // ✅ Nouvelle icône flèche
                    contentDescription = "Envoyer",
                    tint = Color.Red
                )
            }
        }
    }

}

// 🔹 **Fonction pour interroger Gemini AI**
private suspend fun getAIResponse(generativeModel: GenerativeModel, input: String): String {
    return try {
        val response = generativeModel.generateContent(input)
        response.text ?: "Aucune réponse obtenue"
    } catch (e: Exception) {
        "Erreur: ${e.message}"
    }
}
