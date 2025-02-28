package fr.isen.soubry.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import fr.isen.soubry.isensmartcompanion.navigation.BottomNavItem
import fr.isen.soubry.isensmartcompanion.screens.AssistantScreen
import fr.isen.soubry.isensmartcompanion.screens.EventsScreen
import fr.isen.soubry.isensmartcompanion.screens.HistoryScreen
import fr.isen.soubry.isensmartcompanion.screens.AgendaScreen
import fr.isen.soubry.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.soubry.isensmartcompanion.screens.EventDetailScreen
import fr.isen.soubry.isensmartcompanion.screens.HistoryDetailScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.soubry.isensmartcompanion.screens.EventsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Events,
        BottomNavItem.Agenda,
        BottomNavItem.History
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = false,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val eventsViewModel: EventsViewModel = viewModel() // ✅ ViewModel partagé

    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { AssistantScreen() }
        composable(BottomNavItem.Events.route) {
            EventsScreen(navController, eventsViewModel)
        }
        composable(BottomNavItem.History.route) {
            HistoryScreen(navController) // ✅ Passer navController à HistoryScreen
        }
        composable("eventDetail/{eventId}") { backStackEntry ->
            EventDetailScreen(navController, backStackEntry, eventsViewModel)
        }

        composable(BottomNavItem.Agenda.route) { AgendaScreen(navController) }

        composable("historyDetail/{interactionId}") { backStackEntry ->
            HistoryDetailScreen(navController, backStackEntry, viewModel()) // ✅ Ajout de la route pour HistoryDetailScreen
        }
    }
}

