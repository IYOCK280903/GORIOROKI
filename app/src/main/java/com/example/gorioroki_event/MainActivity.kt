package com.example.gorioroki_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gorioroki_event.ui.CreateEventScreen
import com.example.gorioroki_event.ui.EditEventScreen
import com.example.gorioroki_event.ui.EventDetailScreen
import com.example.gorioroki_event.ui.EventListScreen
import com.example.gorioroki_event.ui.QuickActionsScreen // Import layar QuickActions
import com.example.gorioroki_event.ui.theme.GoRioRoki_EventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoRioRoki_EventTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Start destination adalah event_list
    NavHost(navController = navController, startDestination = "event_list") {

        composable("event_list") {
            EventListScreen(navController)
        }

        composable("event_detail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                EventDetailScreen(navController, eventId)
            }
        }

        composable("create_event") {
            CreateEventScreen(navController)
        }

        composable("edit_event/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                EditEventScreen(navController, eventId)
            }
        }

        // Rute baru untuk Quick Actions (Tester Screen)
        composable("quick_actions") {
            QuickActionsScreen(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoRioRoki_EventTheme {
        AppNavigation()
    }
}
