package fr.isen.soubry.isensmartcompanion.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import fr.isen.soubry.isensmartcompanion.models.Event
import fr.isen.soubry.isensmartcompanion.notifications.NotificationViewModel
import fr.isen.soubry.isensmartcompanion.notifications.SharedPreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    eventsViewModel: EventsViewModel,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val eventId = backStackEntry.arguments?.getString("eventId")
    val event = eventsViewModel.events.find { it.id == eventId }

    Scaffold(
        topBar = { EventDetailTopBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (event == null) {
                Text(
                    text = "Événement introuvable",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            } else {
                EventDetailContent(event, notificationViewModel)
            }
        }
    }
}

@Composable
fun EventDetailContent(event: Event, notificationViewModel: NotificationViewModel) {
    val context = LocalContext.current
    var isReminderSet by remember { mutableStateOf(SharedPreferencesManager.isNotificationSubscribed(context, event.title)) }
    val coroutineScope = rememberCoroutineScope()

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = event.title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFB71C1C)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DetailItem("Date", event.date, Icons.Default.CalendarToday)
            DetailItem("Lieu", event.location, Icons.Default.LocationOn)
            DetailItem("Catégorie", event.category, Icons.Default.Category)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = event.description,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                isReminderSet = !isReminderSet
                SharedPreferencesManager.setNotificationSubscribed(context, event.title, isReminderSet)

                if (isReminderSet) {
                    coroutineScope.launch {
                        notificationViewModel.scheduleNotification(context, event.title)
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isReminderSet) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsNone,
                contentDescription = "Activer/Désactiver le rappel",
                tint = if (isReminderSet) Color.Green else Color.Gray
            )
        }
        Text(
            text = if (isReminderSet) "Rappel activé" else "Activer le rappel",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = value, fontSize = 16.sp, color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailTopBar(navController: NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
        },
        title = {
            Text(text = "Détails de l'événement", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    )
}
