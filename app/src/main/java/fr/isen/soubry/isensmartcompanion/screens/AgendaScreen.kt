package fr.isen.soubry.isensmartcompanion.screens

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.CalendarView
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Description


data class Event(val title: String, val date: String, val time: String?, val importance: String?, val description: String?)

fun saveEventsToPreferences(context: Context, events: List<Event>) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(events)
    editor.putString("events_list", json)
    editor.apply()
}

fun loadEventsFromPreferences(context: Context): MutableList<Event> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("events_list", null)
    val type = object : TypeToken<MutableList<Event>>() {}.type
    return gson.fromJson(json, type) ?: mutableListOf()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf(SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE).format(Date())) }
    val eventList = remember { mutableStateListOf<Event>().apply { addAll(loadEventsFromPreferences(context)) } }
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("Ajouter un événement", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(eventList.filter { it.date == selectedDate.value }) { event ->
                    EventItem(event, eventList, coroutineScope, context)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFFFFEBEE), // Arrière-plan rouge clair
            title = {
                Text(
                    "Ajouter un événement",
                    color = Color(0xFFB71C1C), // Texte en rouge ISEN
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = newEventTitle,
                        onValueChange = { newEventTitle = it },
                        label = { Text("Entrez un titre", color = Color.Black) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White, // Fond blanc
                            focusedIndicatorColor = Color(0xFFB71C1C), // Rouge ISEN en focus
                            unfocusedIndicatorColor = Color.Gray // Gris en mode inactif
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // État pour stocker l'heure sélectionnée
                    var showTimePicker by remember { mutableStateOf(false) }
                    var selectedTime by remember { mutableStateOf("") }

                    Button(
                        onClick = { showTimePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) {
                        Text(
                            if (selectedTime.isEmpty()) "Sélectionner l'heure" else "Heure : $selectedTime",
                            color = Color.White
                        )
                    }

                    // Affichage du TimePickerDialog
                    if (showTimePicker) {
                        val context = LocalContext.current
                        val calendar = Calendar.getInstance()
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)

                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                newEventTime = selectedTime
                                showTimePicker = false
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newEventDescription,
                        onValueChange = { newEventDescription = it },
                        label = { Text("Entrez une description", color = Color.Black) },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White, // Fond blanc
                            focusedIndicatorColor = Color(0xFFB71C1C), // Rouge ISEN en focus
                            unfocusedIndicatorColor = Color.Gray // Gris en mode inactif
                        )
                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEventTitle.isNotEmpty()) {
                            eventList.add(
                                Event(
                                    newEventTitle,
                                    selectedDate.value,
                                    newEventTime.takeIf { it.isNotEmpty() },
                                    newEventImportance.takeIf { it.isNotEmpty() },
                                    newEventDescription.takeIf { it.isNotEmpty() }
                                )
                            )
                            saveEventsToPreferences(context, eventList)
                            Toast.makeText(context, "Événement ajouté !", Toast.LENGTH_SHORT).show()
                            newEventTitle = ""
                            newEventTime = ""
                            newEventImportance = ""
                            newEventDescription = ""
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                ) {
                    Text("Ajouter", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Annuler", color = Color.White)
                }
            }
        )
    }

}

@Composable
fun EventItem(event: Event, eventList: MutableList<Event>, coroutineScope: CoroutineScope, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight() // ✅ Hauteur dynamique selon le contenu
            .padding(vertical = 8.dp)
            .clickable {},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Rouge clair
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Bouton poubelle en haut à droite
            IconButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.Main) {
                        eventList.remove(event)
                        saveEventsToPreferences(context, eventList)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd) // ✅ Position en haut à droite
                    .padding(8.dp) // ✅ Ajout d'un peu d'espace
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
                        imageVector = Icons.Filled.Event,
                        contentDescription = "Événement",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                event.time?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "Heure",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = it, color = Color.Black)
                    }
                }

                event.importance?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Importance",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = it, color = Color.Black)
                    }
                }

                event.description?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = "Description",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
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
