package fr.isen.soubry.isensmartcompanion.screens

import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class Event(val title: String, val date: String, val time: String?, val importance: String?, val description: String?)

@Composable
fun AgendaScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf(SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE).format(Date())) }
    val eventList = remember { mutableStateListOf<Event>() }
    var showDialog by remember { mutableStateOf(false) }
    var newEventTitle by remember { mutableStateOf("") }
    var newEventTime by remember { mutableStateOf("") }
    var newEventImportance by remember { mutableStateOf("") }
    var newEventDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AgendaTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AndroidView(factory = { ctx ->
                CalendarView(ctx).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, dayOfMonth)

                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("fr", "FR"))
                        selectedDate.value = dateFormat.format(calendar.time)
                    }
                }
            }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)) // Rouge ISEN
            ) {
                Text("Ajouter un événement", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(eventList.filter { it.date == selectedDate.value }) { event ->
                    EventItem(event, eventList, coroutineScope)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ajouter un événement") },
            text = {
                Column {
                    Text("Titre de l'événement :")
                    TextField(value = newEventTitle, onValueChange = { newEventTitle = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Heure (facultatif) :")
                    TextField(value = newEventTime, onValueChange = { newEventTime = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Degré d'importance (facultatif) :")
                    TextField(value = newEventImportance, onValueChange = { newEventImportance = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Description (facultatif) :")
                    TextField(value = newEventDescription, onValueChange = { newEventDescription = it })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newEventTitle.isNotEmpty()) {
                        eventList.add(Event(newEventTitle, selectedDate.value, newEventTime.takeIf { it.isNotEmpty() }, newEventImportance.takeIf { it.isNotEmpty() }, newEventDescription.takeIf { it.isNotEmpty() }))
                        Toast.makeText(context, "Événement ajouté !", Toast.LENGTH_SHORT).show()
                        newEventTitle = ""
                        newEventTime = ""
                        newEventImportance = ""
                        newEventDescription = ""
                        showDialog = false
                    }
                }) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaTopBar() {
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
fun EventItem(event: Event, eventList: MutableList<Event>, coroutineScope: CoroutineScope) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "📅 ${event.title}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            event.time?.let { Text("🕒 Heure : $it") }
            event.importance?.let { Text("❗ Importance : $it") }
            event.description?.let { Text("📝 Description : $it") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { coroutineScope.launch(Dispatchers.Main) { eventList.remove(event) } }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer", tint = Color.Black)
                }
            }
        }
    }
}