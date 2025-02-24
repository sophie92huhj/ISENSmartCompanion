package fr.isen.soubry.isensmartcompanion.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

@Composable
fun AssistantScreen() {
    var question by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Posez-moi une question...") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // ESPACEMENT POUR DESCENDRE LE TITRE
        Spacer(modifier = Modifier.height(80.dp))

        // TITRE ISEN + Smart Companion
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ISEN",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB71C1C)
            )
            Text(
                text = "Smart Companion",
                fontSize = 24.sp,
                color = Color.Gray
            )
        }

        // ESPACE VIDE AU MILIEU
        Spacer(modifier = Modifier.weight(1f))

        // AFFICHAGE DE LA RÉPONSE
        Text(
            text = response,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // CHAMP DE TEXTE + BOUTON ENVOYER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CHAMP DE TEXTE
            TextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Posez votre question...") },
                textStyle = TextStyle(fontSize = 16.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            // BOUTON ENVOYER
            Button(
                onClick = {
                    response = "Vous avez demandé : $question"
                    Toast.makeText(context, "Question envoyée", Toast.LENGTH_SHORT).show()
                    question = ""
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Envoyer",
                    tint = Color.White
                )
            }
        }
    }
}
