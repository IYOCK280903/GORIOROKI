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
// Pastikan file-file di bawah ini benar-benar ada di folder ui.
// Jika belum ada, beri komentar pada baris import dan pemanggilannya di NavHost.
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
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Ubah startDestination ke "event_list" karena ApiTesterScreen tidak ditemukan
    NavHost(navController = navController, startDestination = "event_list") {

        composable("event_list") {
            EventListScreen(navController)
        }

        composable("event_detail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            // Pastikan EventDetailScreen menerima null handling jika id tidak ada
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
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoRioRoki_EventTheme {
        AppNavigation()
    }
}
