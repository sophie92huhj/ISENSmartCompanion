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
import fr.isen.soubry.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.soubry.isensmartcompanion.screens.EventDetailScreen
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
    // Création d'un ViewModel pour les événements
    val eventsViewModel: EventsViewModel = viewModel() // ✅ Création d'un ViewModel partagé

    // Appel à la page principale et aux autres pages
    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { AssistantScreen() } // Page d'accueil
        composable(BottomNavItem.Events.route) {
            EventsScreen(navController, eventsViewModel) // Page des événements
        }
        composable(BottomNavItem.History.route) { HistoryScreen() } // Page historique
        composable("eventDetail/{eventId}") { backStackEntry ->
            // Page de détails d'un événement
            EventDetailScreen(navController, backStackEntry, eventsViewModel)
        }
    }
}
