package com.example.gorioroki_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gorioroki_event.ui.CreateEventScreen
import com.example.gorioroki_event.ui.EditEventScreen
import com.example.gorioroki_event.ui.EventDetailScreen
import com.example.gorioroki_event.ui.EventListScreen
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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "event_list") {

                        // Halaman List
                        composable("event_list") {
                            EventListScreen(navController)
                        }

                        // Halaman Detail
                        composable("event_detail/{eventId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("eventId")
                            EventDetailScreen(navController, id)
                        }

                        // Halaman Create
                        composable("create_event") {
                            CreateEventScreen(navController)
                        }

                        // Halaman Edit
                        composable("edit_event/{eventId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("eventId")
                            EditEventScreen(navController, id)
                        }
                    }
                }
            }
        }
    }
}
