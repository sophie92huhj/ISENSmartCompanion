package fr.isen.soubry.isensmartcompanion.screens

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
                    Slider(
                        value = newEventImportance.toFloatOrNull() ?: 1f,
                        onValueChange = { newEventImportance = it.toInt().toString() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFB71C1C))
                    )
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
                        saveEventsToPreferences(context, eventList)
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

@Composable
fun EventItem(event: Event, eventList: MutableList<Event>, coroutineScope: CoroutineScope, context: Context) {
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
                IconButton(onClick = {
                    coroutineScope.launch(Dispatchers.Main) {
                        eventList.remove(event)
                        saveEventsToPreferences(context, eventList)
                    }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer", tint = Color.Black)
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
